package com;
import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class MoService {
    public static String JOB_FILE_PATH;
    public static String APP_FILE_PATH;

    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        APP_FILE_PATH = context.getRealPath("data/application.csv");
    }

    // 获取所有岗位（修复空指针）
    public List<Job> getAllJobs() {
        if (JOB_FILE_PATH == null || JOB_FILE_PATH.isEmpty()) {
            return new ArrayList<>();
        }
        return CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
    }

    // 发布岗位
    public Job publishJob(String moId, String password, String subject, String workType,
                          String description, String skillRequirement, int hoursPerWeek, String compensation) {
        if (JOB_FILE_PATH == null) return null;
        if (!AuthUtil.authenticateMO(moId, password)) return null;
        if (moId == null || moId.isBlank() || subject == null || subject.isBlank()
                || workType == null || workType.isBlank() || description == null || description.isBlank()
                || skillRequirement == null || skillRequirement.isBlank() || compensation == null || compensation.isBlank()) {
            return null;
        }

        String randomCode = generateRandomCode(6);
        String jobId = moId + randomCode;

        Job newJob = new Job(
                jobId, moId, subject, workType, description,
                skillRequirement, hoursPerWeek, compensation, "OPEN"
        );

        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        jobList.add(newJob);
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);
        return newJob;
    }

    // ==============================================
    // 录用申请者（返回完整信息，支持页面显示所有字段）
    // ==============================================
    public Application acceptApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) return null;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = getAllJobs();

        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return null;
        if (!"PENDING".equals(targetApp.getAppStatus())) return null;

        // 状态改为已录用
        targetApp.setAppStatus("ACCEPTED");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        // 更新岗位状态
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                job.setStatus("FILLED");
                break;
            }
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);

        return targetApp;
    }

    // 取消录用
    public boolean cancelApplicant(String moId, String cancelAppId) {
        if (moId == null || moId.isBlank() || cancelAppId == null || cancelAppId.isBlank()) return false;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = getAllJobs();

        Application targetApp = null;
        for (Application app : appList) {
            if (cancelAppId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return false;
        if (!"ACCEPTED".equals(targetApp.getAppStatus())) return false;

        targetApp.setAppStatus("PENDING");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                job.setStatus("OPEN");
                break;
            }
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);

        return true;
    }

    // 获取当前 MO 的所有申请（完整信息）
    public List<Application> getAllApps(String moId, String password, boolean isAdmin) {
        if (!AuthUtil.authenticateMO(moId, password)) return null;

        List<Application> allApps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Application> result = new ArrayList<>();
        for (Application app : allApps) {
            if (moId.equals(app.getMoId())) {
                result.add(app);
            }
        }
        return result;
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}