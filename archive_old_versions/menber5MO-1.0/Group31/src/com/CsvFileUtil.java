package com;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvFileUtil {

    // CSV文件分隔符
    private static final String SEPARATOR = ",";
    // 换行符
    private static final String NEW_LINE = "\n";

    /**
     * 写入Job列表到CSV文件
     */
    public static void writeJobListToCsv(String filePath, List<Job> jobList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 写入表头
            writer.write("jobId,moId,jobName,jobRequirements,jobStatus");
            writer.write(NEW_LINE);
            // 写入数据行
            for (Job job : jobList) {
                writer.write(String.join(SEPARATOR,
                        job.getJobId(),
                        job.getMoId(),
                        escapeCsvField(job.getJobName()),
                        escapeCsvField(job.getJobRequirements()),
                        job.getJobStatus()));
                writer.write(NEW_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从CSV文件读取Job列表
     */
    public static List<Job> readJobListFromCsv(String filePath) {
        List<Job> jobList = new ArrayList<>();
        File file = new File(filePath);
        // 文件不存在则创建空文件并返回空列表
        if (!file.exists()) {
            createFileIfNotExists(file);
            return jobList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true; // 跳过表头
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.isBlank()) continue;
                // 解析CSV行
                String[] fields = parseCsvLine(line);
                if (fields.length < 5) continue; // 字段不全则跳过
                Job job = new Job();
                job.setJobId(fields[0]);
                job.setMoId(fields[1]);
                job.setJobName(unescapeCsvField(fields[2]));
                job.setJobRequirements(unescapeCsvField(fields[3]));
                job.setJobStatus(fields[4]);
                jobList.add(job);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    /**
     * 写入Application列表到CSV文件
     */
    public static void writeAppListToCsv(String filePath, List<Application> appList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 写入表头
            writer.write("appId,jobId,taId,appStatus");
            writer.write(NEW_LINE);
            // 写入数据行
            for (Application app : appList) {
                writer.write(String.join(SEPARATOR,
                        app.getAppId(),
                        app.getJobId(),
                        app.getTaId(),
                        app.getAppStatus()));
                writer.write(NEW_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从CSV文件读取Application列表
     */
    public static List<Application> readAppListFromCsv(String filePath) {
        List<Application> appList = new ArrayList<>();
        File file = new File(filePath);
        // 文件不存在则创建空文件并返回空列表
        if (!file.exists()) {
            createFileIfNotExists(file);
            return appList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true; // 跳过表头
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.isBlank()) continue;
                // 解析CSV行
                String[] fields = parseCsvLine(line);
                if (fields.length < 4) continue; // 字段不全则跳过
                Application app = new Application();
                app.setAppId(fields[0]);
                app.setJobId(fields[1]);
                app.setTaId(fields[2]);
                app.setAppStatus(fields[3]);
                appList.add(app);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appList;
    }

    // 辅助方法：处理CSV字段中的分隔符/换行符（转义）
    private static String escapeCsvField(String field) {
        if (field == null) return "";
        // 如果字段包含分隔符、换行符或双引号，需要用双引号包裹，并转义内部的双引号
        if (field.contains(SEPARATOR) || field.contains(NEW_LINE) || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    // 辅助方法：还原转义的CSV字段
    private static String unescapeCsvField(String field) {
        if (field == null) return "";
        // 移除双引号包裹，并还原内部的双引号
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
            return field.replace("\"\"", "\"");
        }
        return field;
    }

    // 辅助方法：解析CSV行（处理带双引号的字段）
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // 切换引号状态
            } else if (c == SEPARATOR.charAt(0) && !inQuotes) {
                // 分隔符且不在引号内，结束当前字段
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        // 添加最后一个字段
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    // 辅助方法：创建文件（含父目录）
    private static void createFileIfNotExists(File file) {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); // 创建父目录
            }
            file.createNewFile(); // 创建文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
 * 从CSV文件读取认证信息列表（适配auth.csv）
 */
public static List<AuthUtil.Auth> readAuthListFromCsv(String filePath) {
    List<AuthUtil.Auth> authList = new ArrayList<>();
    File file = new File(filePath);
    // 文件不存在则创建空文件并返回空列表（首次启动自动创建）
    if (!file.exists()) {
        createFileIfNotExists(file);
        return authList;
    }
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        boolean isFirstLine = true; // 跳过表头（userType,userId,password）
        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            if (line.isBlank()) continue; // 跳过空行
            // 解析CSV行（复用原有解析方法，支持特殊字符）
            String[] fields = parseCsvLine(line);
            if (fields.length < 3) continue; // 字段不全则跳过（至少3个字段）
            // 封装Auth对象
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