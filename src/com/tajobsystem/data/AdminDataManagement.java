package com.tajobsystem.data;

import com.tajobsystem.model.Admin;

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

    public List<Admin> loadPosts() throws IOException {
        ensureCsvExists();
        List<Admin> posts = new ArrayList<>();
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

            posts.add(new Admin(
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

    public void savePosts(List<Admin> posts) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Admin post : posts) {
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
