package TaJob;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Service class for loading TA job postings from CSV and applying search filters.
 *
 * @author Linyao Qi
 * @version 3
 */
public class TaJobService {
    private final Path jobCsvPath;

    /**
     * Creates a job service using the supplied CSV path.
     *
     * @param csvPath path to job.csv; when blank, a default project-relative path is used
     */
    public TaJobService(String csvPath) {
        this.jobCsvPath = resolvePath(csvPath);
    }

    /**
     * Loads all job postings from the configured CSV file.
     *
     * @return list of parsed job postings, or an empty list when the file is missing
     *         or cannot be read
     */
    public List<JobPosting> getAllJobs() {
        if (jobCsvPath == null || !Files.exists(jobCsvPath)) {
            return Collections.emptyList();
        }

        List<JobPosting> jobs = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(jobCsvPath, StandardCharsets.UTF_8)) {
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
                if (fields.size() < 9) {
                    continue;
                }
                jobs.add(new JobPosting(
                        fields.get(0),
                        fields.get(1),
                        fields.get(2),
                        fields.get(3),
                        fields.get(4),
                        fields.get(5),
                        fields.get(6),
                        fields.get(7),
                        fields.get(8)));
            }
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
        return jobs;
    }

    /**
     * Searches jobs using optional subject, work type, status, and keyword filters.
     * All non-empty filters must match for a job to be included.
     *
     * @param subject exact subject filter, or blank for all subjects
     * @param workType exact work type filter, or blank for all types
     * @param status exact status filter, or blank for all statuses
     * @param keyword keyword matched against job text fields, or blank for no keyword
     * @return filtered job postings
     */
    public List<JobPosting> searchJobs(String subject, String workType, String status, String keyword) {
        String subjectFilter = normalize(subject);
        String workTypeFilter = normalize(workType);
        String statusFilter = normalize(status);
        String keywordFilter = normalize(keyword).toLowerCase(Locale.ROOT);

        List<JobPosting> allJobs = getAllJobs();
        if (allJobs.isEmpty()) {
            return allJobs;
        }

        List<JobPosting> result = new ArrayList<>();
        for (JobPosting job : allJobs) {
            if (!subjectFilter.isEmpty() && !subjectFilter.equalsIgnoreCase(job.getSubject())) {
                continue;
            }
            if (!workTypeFilter.isEmpty() && !workTypeFilter.equalsIgnoreCase(job.getWorkType())) {
                continue;
            }
            if (!statusFilter.isEmpty() && !statusFilter.equalsIgnoreCase(job.getStatus())) {
                continue;
            }
            if (!keywordFilter.isEmpty() && !matchesKeyword(job, keywordFilter)) {
                continue;
            }
            result.add(job);
        }
        return result;
    }

    /**
     * Collects unique subjects from all loaded jobs for the filter dropdown.
     *
     * @return ordered set of non-empty subjects
     */
    public Set<String> getAllSubjects() {
        Set<String> subjects = new LinkedHashSet<>();
        for (JobPosting job : getAllJobs()) {
            if (!job.getSubject().isEmpty()) {
                subjects.add(job.getSubject());
            }
        }
        return subjects;
    }

    /**
     * Collects unique work types from all loaded jobs for the filter dropdown.
     *
     * @return ordered set of non-empty work types
     */
    public Set<String> getAllWorkTypes() {
        Set<String> workTypes = new LinkedHashSet<>();
        for (JobPosting job : getAllJobs()) {
            if (!job.getWorkType().isEmpty()) {
                workTypes.add(job.getWorkType());
            }
        }
        return workTypes;
    }

    /**
     * Collects unique job statuses from all loaded jobs for the filter dropdown.
     *
     * @return ordered set of non-empty statuses
     */
    public Set<String> getAllStatuses() {
        Set<String> statuses = new LinkedHashSet<>();
        for (JobPosting job : getAllJobs()) {
            if (!job.getStatus().isEmpty()) {
                statuses.add(job.getStatus());
            }
        }
        return statuses;
    }

    /**
     * Checks whether a keyword appears in the searchable fields of a job.
     *
     * @param job job posting to inspect
     * @param keyword lower-case keyword
     * @return true when the keyword appears in any searchable field
     */
    private boolean matchesKeyword(JobPosting job, String keyword) {
        return containsIgnoreCase(job.getJobId(), keyword)
                || containsIgnoreCase(job.getMoId(), keyword)
                || containsIgnoreCase(job.getSubject(), keyword)
                || containsIgnoreCase(job.getWorkType(), keyword)
                || containsIgnoreCase(job.getDescription(), keyword)
                || containsIgnoreCase(job.getSkillRequirement(), keyword)
                || containsIgnoreCase(job.getCompensation(), keyword);
    }

    /**
     * Performs case-insensitive containment matching with null-safe text handling.
     *
     * @param text source text
     * @param keyword lower-case keyword
     * @return true when text contains the keyword
     */
    private boolean containsIgnoreCase(String text, String keyword) {
        return normalize(text).toLowerCase(Locale.ROOT).contains(keyword);
    }

    /**
     * Normalises nullable text values for comparison.
     *
     * @param value raw value
     * @return trimmed value, or an empty string when null
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Resolves the configured CSV file path.
     *
     * @param csvPath configured path
     * @return resolved CSV path
     */
    private Path resolvePath(String csvPath) {
        if (csvPath != null && !csvPath.trim().isEmpty()) {
            return Paths.get(csvPath);
        }
        return Paths.get(System.getProperty("user.dir"), "web", "data", "job.csv");
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
