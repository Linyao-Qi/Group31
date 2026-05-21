package Admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Loads and saves administrator data stored in CSV files.
 *
 * @author Yutong Yao
 * @version 1.0
 */
public class AdminDataManagement {
    private static final File WORKLOAD_FILE =
            DataFileLocator.resolveDataFile("workloads.csv", AdminDataManagement.class);
    private static final File JOB_FILE =
            DataFileLocator.resolveDataFile("job.csv", AdminDataManagement.class);
    private static final File APPLICATION_FILE =
            DataFileLocator.resolveDataFile("application.csv", AdminDataManagement.class);
    private static final File AUTH_FILE =
            DataFileLocator.resolveDataFile("auth.csv", AdminDataManagement.class);

    /**
     * Loads workload assignments from workloads.csv, creating defaults when the file is missing.
     *
     * @return workload assignments for admin display and editing
     * @throws IOException if the CSV file cannot be read or created
     */
    public List<AdminWorkload> loadWorkloads() throws IOException {
        ensureWorkloadCsvExists();
        List<AdminWorkload> workloads = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(WORKLOAD_FILE))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 7) {
                    continue;
                }

                AdminWorkload workload;
                String status = "Normal";
                if (parts.length >= 9) {
                    workload = new AdminWorkload(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim(),
                            safeParseDouble(parts[6].trim(), 0.0),
                            safeParseDouble(parts[7].trim(), 0.0)
                    );
                    status = parts[8].trim();
                } else {
                    // Compatible with legacy 7-column workload csv.
                    workload = new AdminWorkload(
                            "",
                            "",
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            safeParseDouble(parts[4].trim(), 0.0),
                            safeParseDouble(parts[5].trim(), 0.0)
                    );
                    status = parts.length >= 7 ? parts[6].trim() : "Normal";
                }
                workload.setStatus(status.isEmpty() ? "Normal" : status);
                workloads.add(workload);
            }
        }

        return workloads;
    }

    /**
     * Persists workload assignments to workloads.csv.
     *
     * @param workloads workload assignments to save
     * @throws IOException if the CSV file cannot be written
     */
    public void saveWorkloads(List<AdminWorkload> workloads) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WORKLOAD_FILE, false))) {
            writer.write("moName,moId,taId,taName,moduleName,moduleCode,courseWorkHour,taTotalWorkHour,status");
            writer.newLine();

            for (AdminWorkload workload : workloads) {
                writer.write(workload.getMoName() + ","
                        + workload.getMoId() + ","
                        + workload.getTaId() + ","
                        + workload.getTaName() + ","
                        + workload.getModuleName() + ","
                        + workload.getModuleCode() + ","
                        + workload.getCourseWorkHour() + ","
                        + workload.getTaTotalWorkHour() + ","
                        + workload.getStatus());
                writer.newLine();
            }
        }
    }


    /**
     * Loads recruitment posts from job.csv, creating default posts when the file is missing.
     *
     * @return recruitment posts
     * @throws IOException if the CSV file cannot be read or created
     */
    public List<AdminRecruitment> loadPosts() throws IOException {
        ensureJobCsvExists();
        List<AdminRecruitment> posts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(JOB_FILE))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = parseCsvLine(line);
                if (parts.length < 9) {
                    continue;
                }

                String jobId = parts[0].trim();
                String moId = parts.length >= 2 ? parts[1].trim() : "";
                String subject = unescapeCsvField(parts[2].trim());
                String workType = unescapeCsvField(parts[3].trim());
                String description = unescapeCsvField(parts[4].trim());
                String skillRequirement = unescapeCsvField(parts[5].trim());
                double hoursPerWeek = safeParseDouble(parts[6].trim(), 0.0);
                String compensation = unescapeCsvField(parts[7].trim());
                String jobStatus = unescapeCsvField(parts[8].trim());
                int maxHire = parts.length >= 10 ? safeParseInt(parts[9].trim(), 0) : 0;

                posts.add(new AdminRecruitment(
                        jobId,
                        subject,
                        subject,
                        workType,
                        "",
                        description,
                        skillRequirement,
                        maxHire,
                        "",
                        hoursPerWeek,
                        compensation,
                        "OPEN".equalsIgnoreCase(jobStatus),
                        moId
                ));
            }
        }

        return posts;
    }


    /**
     * Persists recruitment posts to job.csv.
     *
     * @param posts recruitment posts to save
     * @throws IOException if the CSV file cannot be written
     */
    public void savePosts(List<AdminRecruitment> posts) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JOB_FILE, false))) {
            writer.write("jobId,moId,subject,workType,description,skillRequirement,hoursPerWeek,compensation,status,maxHire");
            writer.newLine();

            for (AdminRecruitment post : posts) {
                String jobStatus = post.isOpen() ? "OPEN" : "CLOSED";
                writer.write(post.getJobId() + ","
                        + post.getMoId() + ","
                        + escapeCsvField(post.getSubject()) + ","
                        + escapeCsvField(post.getWorkType()) + ","
                        + escapeCsvField(post.getDescription()) + ","
                        + escapeCsvField(post.getRequirements()) + ","
                        + post.getHoursPerWeek() + ","
                        + escapeCsvField(post.getCompensation()) + ","
                        + jobStatus + ","
                        + post.getOpenPositions());
                writer.newLine();
            }
        }
    }

    /**
     * Loads TA applications from application.csv, creating an empty file when needed.
     *
     * @return application records
     * @throws IOException if the CSV file cannot be read or created
     */
    public List<AdminApplicationRecord> loadApplications() throws IOException {
        ensureApplicationCsvExists();
        List<AdminApplicationRecord> applications = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICATION_FILE))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = parseCsvLine(line);
                if (parts.length < 11) {
                    continue;
                }

                applications.add(new AdminApplicationRecord(
                        parts[0].trim(),
                        unescapeCsvField(parts[1].trim()),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        unescapeCsvField(parts[5].trim()),
                        unescapeCsvField(parts[6].trim()),
                        unescapeCsvField(parts[7].trim()),
                        unescapeCsvField(parts[8].trim()),
                        unescapeCsvField(parts[9].trim()),
                        parts[10].trim()
                ));
            }
        }

        return applications;
    }

    /**
     * Persists TA application records to application.csv.
     *
     * @param applications application records to save
     * @throws IOException if the CSV file cannot be written
     */
    public void saveApplications(List<AdminApplicationRecord> applications) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPLICATION_FILE, false))) {
            writer.write("appId,name,jobId,moId,taId,major,intro,skills,email,CVpath,appStatus");
            writer.newLine();

            for (AdminApplicationRecord application : applications) {
                writer.write(application.getAppId() + ","
                        + escapeCsvField(application.getName()) + ","
                        + application.getJobId() + ","
                        + application.getMoId() + ","
                        + application.getTaId() + ","
                        + escapeCsvField(application.getMajor()) + ","
                        + escapeCsvField(application.getIntro()) + ","
                        + escapeCsvField(application.getSkills()) + ","
                        + escapeCsvField(application.getEmail()) + ","
                        + escapeCsvField(application.getCvPath()) + ","
                        + application.getAppStatus());
                writer.newLine();
            }
        }
    }

    /**
     * Checks admin credentials against auth.csv.
     *
     * @param username administrator username
     * @param password administrator password
     * @return true when a matching ADMIN row exists; false otherwise
     * @throws IOException if auth.csv cannot be read
     */
    public boolean validateAdminCredentials(String username, String password) throws IOException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        if (!AUTH_FILE.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(AUTH_FILE))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = parseCsvLine(line);
                if (parts.length < 3) {
                    continue;
                }
                if ("ADMIN".equalsIgnoreCase(parts[0].trim())
                        && username.trim().equals(parts[1].trim())
                        && password.equals(parts[2].trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void ensureWorkloadCsvExists() throws IOException {
        if (WORKLOAD_FILE.exists()) {
            return;
        }
        createParentFolder(WORKLOAD_FILE);
        saveWorkloads(defaultWorkloads());
    }

    private void ensureJobCsvExists() throws IOException {
        if (JOB_FILE.exists()) {
            return;
        }
        createParentFolder(JOB_FILE);
        savePosts(defaultPosts());
    }

    private void ensureApplicationCsvExists() throws IOException {
        if (APPLICATION_FILE.exists()) {
            return;
        }
        createParentFolder(APPLICATION_FILE);
        saveApplications(new ArrayList<>());
    }

    private void createParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private List<AdminWorkload> defaultWorkloads() {
        List<AdminWorkload> defaults = new ArrayList<>();
        defaults.add(new AdminWorkload("Amy", "MO001", "TA001", "Alice", "Data Structures", "CS101", 6, 20));
        defaults.add(new AdminWorkload("Amy", "MO001", "TA001", "Alice", "Programming Basics", "CS100", 8, 20));
        defaults.add(new AdminWorkload("Amy", "MO001", "TA001", "Alice", "Programming Basics", "CS100", 6, 20));
        defaults.add(new AdminWorkload("Amy", "MO001", "TA002", "Bob", "Algorithms", "CS102", 9, 22));
        defaults.add(new AdminWorkload("Amy", "MO001", "TA002", "Bob", "Algorithms", "CS102", 7, 22));
        defaults.add(new AdminWorkload("Amy", "MO001", "TA002", "Bob", "Data Structures", "CS101", 6, 22));
        defaults.add(new AdminWorkload("Brian", "MO002", "TA003", "Cindy", "Databases", "CS103", 5, 11));
        defaults.add(new AdminWorkload("Brian", "MO002", "TA003", "Cindy", "Software Testing", "CS205", 6, 11));
        defaults.add(new AdminWorkload("Brian", "MO002", "TA004", "David", "Networks", "CS104", 10, 19));
        defaults.add(new AdminWorkload("Brian", "MO002", "TA004", "David", "Operating Systems", "CS204", 9, 19));
        defaults.add(new AdminWorkload("Iris", "MO003", "TA005", "Eva", "Computer Architecture", "CS202", 8, 18));
        defaults.add(new AdminWorkload("Iris", "MO003", "TA005", "Eva", "Computer Architecture", "CS202", 10, 18));
        defaults.add(new AdminWorkload("Iris", "MO003", "TA006", "Frank", "Machine Learning", "CS301", 7, 12));
        defaults.add(new AdminWorkload("Iris", "MO003", "TA006", "Frank", "Data Mining", "CS302", 5, 12));
        defaults.add(new AdminWorkload("Liam", "MO004", "TA007", "Grace", "Capstone Project", "CS401", 12, 23));
        defaults.add(new AdminWorkload("Liam", "MO004", "TA007", "Grace", "Capstone Project", "CS401", 11, 23));
        defaults.add(new AdminWorkload("Liam", "MO004", "TA008", "Henry", "Cloud Computing", "CS303", 8, 16));
        defaults.add(new AdminWorkload("Liam", "MO004", "TA008", "Henry", "Software Engineering", "CS206", 8, 16));
        return defaults;
    }

    private List<AdminRecruitment> defaultPosts() {
        List<AdminRecruitment> defaults = new ArrayList<>();
        defaults.add(new AdminRecruitment(
                "J001", "Programming Fundamentals", "Programming Fundamentals", "On-site", "Computer Science",
                "Support weekly lab sessions", "Java and basic debugging", 3,
                "2026-05-01", 8, "18/hour", true, "MO001"
        ));
        defaults.add(new AdminRecruitment(
                "J002", "Algorithms", "Algorithms", "Hybrid", "Computer Science",
                "Run tutorial Q&A", "Algorithms foundation", 2,
                "2026-05-05", 6, "20/hour", true, "MO001"
        ));
        defaults.add(new AdminRecruitment(
                "J003", "Data Management", "Data Management", "Remote", "Computer Science",
                "Mark assignments weekly", "Fair grading experience", 4,
                "2026-04-25", 10, "22/hour", false, "MO002"
        ));
        return defaults;
    }

    private double safeParseDouble(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private int safeParseInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private String unescapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.startsWith("\"") && field.endsWith("\"") && field.length() >= 2) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }
}

