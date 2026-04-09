package com;
import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class MoService {
    public static String JOB_FILE_PATH;
    public static String APP_FILE_PATH;

    // ========== 初始化路径 ==========
    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        APP_FILE_PATH = context.getRealPath("data/application.csv");
    }

    public List<Job> getAllJobs() {
        if (JOB_FILE_PATH == null || JOB_FILE_PATH.isEmpty()) {
            return new ArrayList<>();
        }
        return CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
    }

    // ========== 发布岗位（无默认值，完全按你要求） ==========
    public Job publishJob(String moId, String password, String subject, String workType,
                          String description, String skillRequirement, int hoursPerWeek, String compensation) {

        if (JOB_FILE_PATH == null) {
            System.out.println("Error: JOB_FILE_PATH is null");
            return null;
        }
        if (!AuthUtil.authenticateMO(moId, password)) {
            return null;
        }
        if (moId == null || moId.isBlank() || subject == null || subject.isBlank()
                || workType == null || workType.isBlank() || description == null || description.isBlank()
                || skillRequirement == null || skillRequirement.isBlank() || compensation == null || compensation.isBlank()) {
            return null;
        }

        String randomCode = generateRandomCode(6);
        String jobId = moId + randomCode;

        Job newJob = new Job(
            jobId,
            moId,
            subject,
            workType,
            description,
            skillRequirement,
            hoursPerWeek,
            compensation,
            "OPEN"
        );

        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        List<Job> newJobList = new ArrayList<>(jobList);
        newJobList.add(newJob);
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, newJobList);

        return newJob;
    }

    // ========== 录用申请者 ==========
    public Application acceptApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            return null;
        }

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = getAllJobs();

        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return null;

        Job targetJob = null;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                break;
            }
        }
        if (targetJob == null || !moId.equals(targetJob.getMoId())) return null;
        if (!"PENDING".equals(targetApp.getAppStatus())) return null;

        for (Application app : appList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("ACCEPTED");
                targetApp = app;
                break;
            }
        }
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        for (Job job : jobList) {
            if (targetJob.getJobId().equals(job.getJobId())) {
                job.setStatus("FILLED");
                break;
            }
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);

        return targetApp;
    }

    // ========== 取消录用 ==========
    public boolean cancelApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            return false;
        }

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = getAllJobs();

        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && "ACCEPTED".equals(app.getAppStatus())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return false;

        Job targetJob = null;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                break;
            }
        }
        if (targetJob == null || !moId.equals(targetJob.getMoId())) return false;

        for (Application app : appList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("PENDING");
                break;
            }
        }
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        for (Job job : jobList) {
            if (targetJob.getJobId().equals(job.getJobId())) {
                job.setStatus("OPEN");
                break;
            }
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);

        return true;
    }

    public List<Application> getAllApps(String userId, String password, boolean isAdmin) {
        if (!AuthUtil.authenticate(userId, password, isAdmin)) {
            return null;
        }
        return CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
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