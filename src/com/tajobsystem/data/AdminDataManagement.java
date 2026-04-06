package com.tajobsystem.data;

import com.tajobsystem.model.Admin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminDataManagement {
    private static final String DEFAULT_FILE_PATH = "data/jobs.csv";
    private static final Pattern HOUR_RANGE_PATTERN = Pattern.compile("^\\s*(\\d+(?:\\.\\d+)?)\\s*-\\s*(\\d+(?:\\.\\d+)?)\\s*$");

    public void ensureCsvExists() throws IOException {
        File file = new File(DEFAULT_FILE_PATH);
        if (file.exists()) {
            return;
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        List<Admin> defaults = new ArrayList<>();
        defaults.add(new Admin(
                "J001", "TA - Lab Support", "CS101", "On-site", "Computer Science",
                "Support weekly lab sessions", "Java and basic debugging", 3,
                "2026-05-01", 8, "18/hour", true, "MO001"
        ));
        defaults.add(new Admin(
                "J002", "TA - Tutorial Support", "CS102", "Hybrid", "Computer Science",
                "Run tutorial Q&A", "Algorithms foundation", 2,
                "2026-05-05", 6, "20/hour", true, "MO001"
        ));
        defaults.add(new Admin(
                "J003", "TA - Assignment Marking", "CS201", "Remote", "Computer Science",
                "Mark assignments weekly", "Fair grading experience", 4,
                "2026-04-25", 10, "22/hour", false, "MO002"
        ));
        savePosts(defaults);
    }

    public List<Admin> loadPosts() throws IOException {
        List<Admin> posts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(DEFAULT_FILE_PATH))) {
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
                if (parts.length < 13) {
                    continue;
                }

                String jobId = parts[0].trim();
                String title = parts[1].trim();
                String subject = parts[2].trim();
                String workType = parts[3].trim();
                String department = parts[4].trim();
                String description = parts[5].trim();
                String requirements = parts[6].trim();
                int openPositions = Integer.parseInt(parts[7].trim());
                String deadline = parts[8].trim();
                double hoursPerWeek = parseHoursPerWeek(parts[9].trim());
                String compensation = parts[10].trim();
                boolean open = Boolean.parseBoolean(parts[11].trim());
                String moId = parts[12].trim();

                posts.add(new Admin(
                        jobId,
                        title,
                        subject,
                        workType,
                        department,
                        description,
                        requirements,
                        openPositions,
                        deadline,
                        hoursPerWeek,
                        compensation,
                        open,
                        moId
                ));
            }
        }

        return posts;
    }

    public void savePosts(List<Admin> posts) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DEFAULT_FILE_PATH, false))) {
            writer.write("jobId,title,subject,workType,department,description,requirements,openPositions,deadline,hoursPerWeek,compensation,open,moId");
            writer.newLine();

            for (Admin post : posts) {
                writer.write(post.getJobId() + ","
                        + post.getTitle() + ","
                        + post.getSubject() + ","
                        + post.getWorkType() + ","
                        + post.getDepartment() + ","
                        + post.getDescription() + ","
                        + post.getRequirements() + ","
                        + post.getOpenPositions() + ","
                        + post.getDeadline() + ","
                        + post.getHoursPerWeek() + ","
                        + post.getCompensation() + ","
                        + post.isOpen() + ","
                        + post.getMoId());
                writer.newLine();
            }
        }
    }

    private double parseHoursPerWeek(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            Matcher rangeMatcher = HOUR_RANGE_PATTERN.matcher(value);
            if (rangeMatcher.matches()) {
                double min = Double.parseDouble(rangeMatcher.group(1));
                double max = Double.parseDouble(rangeMatcher.group(2));
                return (min + max) / 2.0;
            }
            throw new NumberFormatException("Invalid hoursPerWeek value: " + value);
        }
    }
}
