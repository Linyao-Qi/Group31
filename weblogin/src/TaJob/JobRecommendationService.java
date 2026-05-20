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

/**
 * Service class that builds explainable job recommendations for the logged-in TA.
 * <p>
 * The recommendation score combines skill overlap, major relevance, and profile
 * completeness. Skills that are not present in the TA profile are treated as
 * items to confirm rather than proof that the TA lacks those skills.
 *
 * @author Linyao Qi
 * @version 3
 */
public class JobRecommendationService {
    private final TaJobService jobService;
    private final Path profileCsvPath;

    /**
     * Creates the recommendation service with job and profile CSV locations.
     *
     * @param jobCsvPath path to job.csv
     * @param profileCsvPath path to profiles.csv
     */
    public JobRecommendationService(String jobCsvPath, String profileCsvPath) {
        this.jobService = new TaJobService(jobCsvPath);
        this.profileCsvPath = resolvePath(profileCsvPath);
    }

    /**
     * Loads the profile snapshot for a TA from profiles.csv.
     *
     * @param taId TA identifier
     * @return profile snapshot, or null when no profile is found
     */
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

    /**
     * Generates recommendations for a TA ID.
     *
     * @param taId TA identifier
     * @return recommended open jobs sorted by descending match rate
     */
    public List<JobRecommendation> recommendJobs(String taId) {
        return recommendJobs(getProfile(taId));
    }

    /**
     * Generates recommendations for a given TA profile snapshot.
     *
     * @param profile current TA profile, or null when the profile is incomplete
     * @return recommended open jobs sorted by descending match rate
     */
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

    /**
     * Calculates the match rate and explanation for one job.
     *
     * @param job job posting to score
     * @param profileSkills normalised skills from the TA profile
     * @param major normalised TA major
     * @return recommendation result for the job
     */
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

    /**
     * Calculates whether the TA major is related to the job subject or description.
     *
     * @param major normalised TA major
     * @param job job posting
     * @return 20 when the major appears related, otherwise 0
     */
    private int calculateMajorScore(String major, JobPosting job) {
        if (major.isEmpty()) {
            return 0;
        }
        String subject = normalize(job.getSubject());
        String searchable = normalize(job.getSubject() + " " + job.getDescription() + " " + job.getWorkType());
        return searchable.contains(major) || (!subject.isEmpty() && major.contains(subject)) ? 20 : 0;
    }

    /**
     * Builds an explanation for the recommendation result.
     *
     * @param profileSkills normalised TA skills
     * @param majorScore score contributed by major relevance
     * @param matchedSkills skills matched between profile and job
     * @param missingSkills listed job skills not found in the profile
     * @return human-readable recommendation reason
     */
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

    /**
     * Checks whether a required skill is compatible with any profile skill.
     * Partial containment is allowed to handle simple variations in wording.
     *
     * @param profileSkills normalised profile skills
     * @param requiredSkill normalised job skill requirement
     * @return true when the skill can be matched
     */
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

    /**
     * Splits comma- or separator-delimited skill text into normalised terms.
     *
     * @param value raw skill text
     * @return ordered set of normalised terms
     */
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

    /**
     * Normalises nullable text values for comparison.
     *
     * @param value raw value
     * @return trimmed lower-case value, or an empty string when null
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Resolves the profile CSV path.
     *
     * @param csvPath configured path
     * @return resolved path to profiles.csv
     */
    private Path resolvePath(String csvPath) {
        if (csvPath == null || csvPath.trim().isEmpty()) {
            return Paths.get(System.getProperty("user.dir"), "web", "data", "profiles.csv");
        }
        return Paths.get(csvPath);
    }

    /**
     * Parses one CSV row while supporting quoted fields and escaped quotes.
     *
     * @param line raw CSV row
     * @return parsed field values
     */
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
