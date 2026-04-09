package com;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvFileUtil {
    private static final String SEPARATOR = ",";
    private static final String NEW_LINE = "\n";

    // ====================== Job 读写不变 ======================
    public static void writeJobListToCsv(String filePath, List<Job> jobList) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write("jobId,moId,subject,workType,description,skillRequirement,hoursPerWeek,compensation,status");
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
                        job.getStatus()
                ));
                writer.write(NEW_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                jobList.add(job);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    // ====================== Application 重写 ======================
    public static void writeAppListToCsv(String filePath, List<Application> appList) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            // 新表头
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
                // 新CSV至少11列
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

    // ====================== 以下全部不变 ======================
    private static String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(SEPARATOR) || field.contains(NEW_LINE) || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private static String unescapeCsvField(String field) {
        if (field == null) return "";
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
            return field.replace("\"\"", "\"");
        }
        return field;
    }

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