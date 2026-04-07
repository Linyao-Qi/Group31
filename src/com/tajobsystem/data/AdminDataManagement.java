package com.tajobsystem.data;

import com.tajobsystem.model.AdminRecruitment;
import com.tajobsystem.model.AdminWorkload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminDataManagement {
    private static final String DEFAULT_FILE_PATH = "data/jobs.csv";
    private static final String WORKLOAD_FILE_PATH = "data/workloads.csv";
    private static final String HEADER =
            "jobId,title,subject,workType,department,description,requirements," +
            "openPositions,deadline,hoursPerWeek,compensation,open,moId";
    private static final Pattern HOUR_RANGE_PATTERN =
            Pattern.compile("^\\s*(\\d+(?:\\.\\d+)?)\\s*-\\s*(\\d+(?:\\.\\d+)?)\\s*$");
    private static final Pattern FIRST_NUMBER_PATTERN =
            Pattern.compile("(\\d+(?:\\.\\d+)?)");

    public void ensureCsvExists() throws IOException {
        File file = new File(DEFAULT_FILE_PATH);
        if (file.exists()) {
            return;
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Cannot create directory: " + parent.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();
        }
    }

    public List<AdminRecruitment> loadPosts() throws IOException {
        ensureCsvExists();
        List<AdminRecruitment> posts = new ArrayList<>();
        List<String> lines = CsvUtil.readAllLines(DEFAULT_FILE_PATH);
        for (String line : lines) {
            String[] parts = CsvUtil.splitLine(line);
            if (parts.length < 13) {
                continue;
            }

            String jobId = normalize(parts[0]);
            if (jobId.isEmpty()) {
                continue;
            }

            posts.add(new AdminRecruitment(
                    jobId,
                    normalize(parts[1]),
                    normalize(parts[2]),
                    normalize(parts[3]),
                    normalize(parts[4]),
                    normalize(parts[5]),
                    normalize(parts[6]),
                    parseOpenPositions(parts[7]),
                    normalize(parts[8]),
                    parseHoursPerWeek(parts[9]),
                    normalize(parts[10]),
                    parseBoolean(parts[11]),
                    normalize(parts[12])
            ));
        }
        return posts;
    }

    public void savePosts(List<AdminRecruitment> posts) throws IOException {
        List<String> lines = new ArrayList<>();
        for (AdminRecruitment post : posts) {
            lines.add(
                    CsvUtil.quoteField(post.getJobId()) + "," +
                    CsvUtil.quoteField(post.getTitle()) + "," +
                    CsvUtil.quoteField(post.getSubject()) + "," +
                    CsvUtil.quoteField(post.getWorkType()) + "," +
                    CsvUtil.quoteField(post.getDepartment()) + "," +
                    CsvUtil.quoteField(post.getDescription()) + "," +
                    CsvUtil.quoteField(post.getRequirements()) + "," +
                    post.getOpenPositions() + "," +
                    CsvUtil.quoteField(post.getDeadline()) + "," +
                    post.getHoursPerWeek() + "," +
                    CsvUtil.quoteField(post.getCompensation()) + "," +
                    post.isOpen() + "," +
                    CsvUtil.quoteField(post.getMoId())
            );
        }
        CsvUtil.writeAllLines(DEFAULT_FILE_PATH, HEADER, lines);
    }

    public List<AdminWorkload> loadWorkloads() throws IOException {
        ensureWorkloadCsvExists();
        List<AdminWorkload> workloads = new ArrayList<>();

        for (String line : CsvUtil.readAllLines(WORKLOAD_FILE_PATH)) {
            String[] parts = CsvUtil.splitLine(line);
            if (parts.length < 8) {
                continue;
            }

            AdminWorkload workload = new AdminWorkload(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim(),
                    Double.parseDouble(parts[6].trim()),
                    Double.parseDouble(parts[7].trim())
            );
            String status = parts.length >= 9 ? parts[8].trim() : "Normal";
            workload.setStatus(status.isEmpty() ? "Normal" : status);
            workloads.add(workload);
        }

        return workloads;
    }

    public void saveWorkloads(List<AdminWorkload> workloads) throws IOException {
        List<String> lines = new ArrayList<>();
        for (AdminWorkload workload : workloads) {
            lines.add(
                    CsvUtil.quoteField(workload.getMoName()) + "," +
                    CsvUtil.quoteField(workload.getMoId()) + "," +
                    CsvUtil.quoteField(workload.getTaId()) + "," +
                    CsvUtil.quoteField(workload.getTaName()) + "," +
                    CsvUtil.quoteField(workload.getModuleName()) + "," +
                    CsvUtil.quoteField(workload.getModuleCode()) + "," +
                    workload.getCourseWorkHour() + "," +
                    workload.getTaTotalWorkHour() + "," +
                    CsvUtil.quoteField(workload.getStatus())
            );
        }
        CsvUtil.writeAllLines(
                WORKLOAD_FILE_PATH,
                "moName,moId,taId,taName,moduleName,moduleCode,courseWorkHour,taTotalWorkHour,status",
                lines
        );
    }

    private void ensureWorkloadCsvExists() throws IOException {
        File file = new File(WORKLOAD_FILE_PATH);
        if (file.exists()) {
            return;
        }
        createParentFolder(file);
        saveWorkloads(defaultWorkloads());
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

    private int parseOpenPositions(String value) {
        String trimmed = normalize(value);
        if (trimmed.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(trimmed);
    }

    private boolean parseBoolean(String value) {
        return Boolean.parseBoolean(normalize(value));
    }

    private double parseHoursPerWeek(String value) {
        String trimmed = normalize(value);
        if (trimmed.isEmpty()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
            Matcher rangeMatcher = HOUR_RANGE_PATTERN.matcher(trimmed);
            if (rangeMatcher.matches()) {
                double min = Double.parseDouble(rangeMatcher.group(1));
                double max = Double.parseDouble(rangeMatcher.group(2));
                return (min + max) / 2.0;
            }

            Matcher firstNumber = FIRST_NUMBER_PATTERN.matcher(trimmed);
            if (firstNumber.find()) {
                return Double.parseDouble(firstNumber.group(1));
            }
            throw new NumberFormatException("Invalid hoursPerWeek value: " + value);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
