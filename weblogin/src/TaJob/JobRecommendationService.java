package TaJob;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class JobRecommendationService {
    private final TaJobService jobService;
    private final Path profileCsvPath;

    public JobRecommendationService(String jobCsvPath, String profileCsvPath) {
        this.jobService = new TaJobService(jobCsvPath);
        this.profileCsvPath = resolvePath(profileCsvPath);
    }

    public TaProfileSnapshot getProfile(String taId) {
        String targetTaId = normalize(taId);
        if (targetTaId.isEmpty() || profileCsvPath == null || !Files.exists(profileCsvPath)) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(profileCsvPath, StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> fields = parseCsvLine(line);
                if (fields.size() < 4) {
                    continue;
                }
                if (targetTaId.equalsIgnoreCase(fields.get(0).trim())) {
                    return new TaProfileSnapshot(
                            fields.get(0),
                            fields.size() > 1 ? fields.get(1) : "",
                            fields.size() > 2 ? fields.get(2) : "",
                            fields.size() > 3 ? fields.get(3) : "",
                            fields.size() > 4 ? fields.get(4) : "");
                }
            }
        } catch (IOException ignored) {
            return null;
        }
        return null;
    }

    public List<JobRecommendation> recommendJobs(String taId) {
        return recommendJobs(getProfile(taId));
    }

    public List<JobRecommendation> recommendJobs(TaProfileSnapshot profile) {
        List<JobRecommendation> recommendations = new ArrayList<>();
        Set<String> profileSkills = splitTerms(profile == null ? "" : profile.getSkills());
        String major = normalize(profile == null ? "" : profile.getMajor());

        for (JobPosting job : jobService.getAllJobs()) {
            if (!"OPEN".equalsIgnoreCase(job.getStatus())) {
                continue;
            }
            recommendations.add(scoreJob(job, profileSkills, major));
        }

        recommendations.sort(Comparator
                .comparingInt(JobRecommendation::getScore).reversed()
                .thenComparing(r -> normalize(r.getJob().getSubject()))
                .thenComparing(r -> normalize(r.getJob().getJobId())));
        return recommendations;
    }

    private JobRecommendation scoreJob(JobPosting job, Set<String> profileSkills, String major) {
        Set<String> requiredSkills = splitTerms(job.getSkillRequirement());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String requiredSkill : requiredSkills) {
            if (hasCompatibleSkill(profileSkills, requiredSkill)) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }

        int skillScore = requiredSkills.isEmpty()
                ? (profileSkills.isEmpty() ? 0 : 45)
                : (int) Math.round((matchedSkills.size() * 70.0) / requiredSkills.size());
        int majorScore = calculateMajorScore(major, job);
        int profileCompletenessScore = profileSkills.isEmpty() ? 0 : 10;
        int totalScore = Math.min(100, skillScore + majorScore + profileCompletenessScore);

        return new JobRecommendation(
                job,
                totalScore,
                matchedSkills,
                missingSkills,
                buildReason(profileSkills, majorScore, matchedSkills, missingSkills));
    }

    private int calculateMajorScore(String major, JobPosting job) {
        if (major.isEmpty()) {
            return 0;
        }
        String subject = normalize(job.getSubject());
        String searchable = normalize(job.getSubject() + " " + job.getDescription() + " " + job.getWorkType());
        return searchable.contains(major) || (!subject.isEmpty() && major.contains(subject)) ? 20 : 0;
    }

    private String buildReason(Set<String> profileSkills, int majorScore,
                               List<String> matchedSkills, List<String> missingSkills) {
        if (profileSkills.isEmpty()) {
            return "Complete your TA profile skills to receive more accurate recommendations.";
        }
        if (!matchedSkills.isEmpty() && missingSkills.isEmpty()) {
            return "Strong recommendation: your current profile matches all listed required skills.";
        }
        if (!matchedSkills.isEmpty()) {
            return majorScore > 0
                    ? "Recommended because your current profile skills and major both match this job."
                    : "Recommended because your current profile matches part of this job requirement.";
        }
        return majorScore > 0
                ? "Possible match based on your major. Confirm whether your profile should include the listed skills."
                : "Limited profile match. Confirm these listed skills or update your profile before applying.";
    }

    private boolean hasCompatibleSkill(Set<String> profileSkills, String requiredSkill) {
        for (String profileSkill : profileSkills) {
            if (profileSkill.equals(requiredSkill)
                    || profileSkill.contains(requiredSkill)
                    || requiredSkill.contains(profileSkill)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> splitTerms(String value) {
        Set<String> terms = new LinkedHashSet<>();
        if (value == null) {
            return terms;
        }
        String[] parts = value.split("[,;/|]+");
        for (String part : parts) {
            String term = normalize(part);
            if (!term.isEmpty()) {
                terms.add(term);
            }
        }
        return terms;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private Path resolvePath(String csvPath) {
        if (csvPath == null || csvPath.trim().isEmpty()) {
            return Paths.get(System.getProperty("user.dir"), "web", "data", "profiles.csv");
        }
        return Paths.get(csvPath);
    }

    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields;
    }
}
