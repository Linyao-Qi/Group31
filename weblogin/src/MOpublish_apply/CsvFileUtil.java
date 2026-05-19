package com;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV File Operation Utility
 * <p>Provides unified CSV read/write functionality for Job, Application, and Auth data.
 * Handles field escaping, parsing, file creation, and UTF-8 encoding for persistent storage.
 * Centralizes all file I/O operations for the TA recruitment system.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class CsvFileUtil {

    /** CSV field separator */
    private static final String SEPARATOR = ",";

    /** New line delimiter for CSV files */
    private static final String NEW_LINE = "\n";

    // ====================== Job Read/Write ======================

    /**
     * Write a list of Job objects to CSV file
     * @param filePath target file path
     * @param jobList list of Job entities to save
     */
    public static void writeJobListToCsv(String filePath, List<Job> jobList) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write("jobId,moId,subject,workType,description,skillRequirement,hoursPerWeek,compensation,status,maxHire");
            writer.write(NEW_LINE);
            for (Job job : jobList) {
                writer.write(String.join(SEPARATOR,
                        job.getJobId(),
                        job.getMoId(),
                        escapeCsvField(job.getSubject()),
                        escapeCsvField(job.getWorkType()),
                        escapeCsvField(job.getDescription()),
                        escapeCsvField(job.getSkillRequirement()),
                        String.valueOf(job.getHoursPerWeek()),
                        escapeCsvField(job.getCompensation()),
                        job.getStatus(),
                        String.valueOf(job.getMaxHire())
                ));
                writer.write(NEW_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read and parse Job list from CSV file
     * @param filePath source CSV path
     * @return List of Job entities
     */
    public static List<Job> readJobListFromCsv(String filePath) {
        List<Job> jobList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            createFileIfNotExists(file);
            return jobList;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.isBlank()) continue;
                String[] fields = parseCsvLine(line);
                if (fields.length < 9) continue;
                Job job = new Job();
                job.setJobId(fields[0]);
                job.setMoId(fields[1]);
                job.setSubject(unescapeCsvField(fields[2]));
                job.setWorkType(unescapeCsvField(fields[3]));
                job.setDescription(unescapeCsvField(fields[4]));
                job.setSkillRequirement(unescapeCsvField(fields[5]));
                try {
                    job.setHoursPerWeek(Integer.parseInt(fields[6].trim()));
                } catch (Exception e) {
                    job.setHoursPerWeek(0);
                }
                job.setCompensation(unescapeCsvField(fields[7]));
                job.setStatus(fields[8]);

                int maxHire = 1;
                try {
                    if (fields.length >= 10 && fields[9] != null && !fields[9].isBlank()) {
                        maxHire = Integer.parseInt(fields[9].trim());
                    }
                } catch (Exception ignored) {}
                job.setMaxHire(maxHire);

                jobList.add(job);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    // ====================== Application Read/Write ======================

    /**
     * Write Application list to CSV file
     * @param filePath target file path
     * @param appList list of Application entities
     */
    public static void writeAppListToCsv(String filePath, List<Application> appList) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write("appId,name,jobId,moId,taId,major,intro,skills,email,CVpath,appStatus");
            writer.write(NEW_LINE);
            for (Application app : appList) {
                writer.write(String.join(SEPARATOR,
                        app.getAppId(),
                        escapeCsvField(app.getName()),
                        app.getJobId(),
                        app.getMoId(),
                        app.getTaId(),
                        escapeCsvField(app.getMajor()),
                        escapeCsvField(app.getIntro()),
                        escapeCsvField(app.getSkills()),
                        escapeCsvField(app.getEmail()),
                        escapeCsvField(app.getCVpath()),
                        app.getAppStatus()
                ));
                writer.write(NEW_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read and parse Application list from CSV
     * @param filePath source CSV path
     * @return List of Application entities
     */
    public static List<Application> readAppListFromCsv(String filePath) {
        List<Application> appList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            createFileIfNotExists(file);
            return appList;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.isBlank()) continue;
                String[] fields = parseCsvLine(line);
                if (fields.length < 11) continue;

                Application app = new Application();
                app.setAppId(fields[0]);
                app.setName(unescapeCsvField(fields[1]));
                app.setJobId(fields[2]);
                app.setMoId(fields[3]);
                app.setTaId(fields[4]);
                app.setMajor(unescapeCsvField(fields[5]));
                app.setIntro(unescapeCsvField(fields[6]));
                app.setSkills(unescapeCsvField(fields[7]));
                app.setEmail(unescapeCsvField(fields[8]));
                app.setCVpath(unescapeCsvField(fields[9]));
                app.setAppStatus(fields[10]);

                appList.add(app);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appList;
    }

    // ====================== Core Helper Methods ======================

    /**
     * Escape special characters for CSV field safety
     * @param field original string
     * @return escaped CSV-compatible string
     */
    private static String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(SEPARATOR) || field.contains(NEW_LINE) || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Unescape CSV field to original text
     * @param field escaped string
     * @return original raw string
     */
    private static String unescapeCsvField(String field) {
        if (field == null) return "";
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
            return field.replace("\"\"", "\"");
        }
        return field;
    }

    /**
     * Parse a single CSV line with quote support
     * @param line raw CSV line
     * @return string array of parsed fields
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == SEPARATOR.charAt(0) && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Create file and parent directories if missing
     * @param file target file
     */
    private static void createFileIfNotExists(File file) {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read user authentication data from auth.csv
     * @param filePath auth file path
     * @return List of Auth entities
     */
    public static List<AuthUtil.Auth> readAuthListFromCsv(String filePath) {
        List<AuthUtil.Auth> authList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            createFileIfNotExists(file);
            return authList;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.isBlank()) continue;
                String[] fields = parseCsvLine(line);
                if (fields.length < 3) continue;
                AuthUtil.Auth auth = new AuthUtil.Auth();
                auth.setUserType(fields[0]);
                auth.setUserId(fields[1]);
                auth.setPassword(fields[2]);
                authList.add(auth);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authList;
    }
}
