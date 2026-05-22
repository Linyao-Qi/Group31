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

    // ========== 发布岗位（带安全测试点，不影响逻辑） ==========
    public Job publishJob(String moId, String password, String jobName, String jobRequirements) {

        // ==========================
        // 【测试点 1：检查路径是否为 null】
        // ==========================
        if (JOB_FILE_PATH == null) {
            System.out.println("【错误】JOB_FILE_PATH 路径为 null");
            return null;
        }
        System.out.println("【测试】JOB_FILE_PATH = " + JOB_FILE_PATH);

        // MO 身份认证
        if (!AuthUtil.authenticateMO(moId, password)) {
            System.out.println("【测试】MO 认证失败");
            return null;
        }

        // 参数非空校验
        if (moId == null || moId.isBlank()
                || jobName == null || jobName.isBlank()
                || jobRequirements == null || jobRequirements.isBlank()) {
            System.out.println("【测试】参数为空");
            return null;
        }

        // 生成 6 位随机码
        String randomCode = generateRandomCode(6);
        String jobId = moId + randomCode;

        Job newJob = new Job(jobId, moId, jobName, jobRequirements, "OPEN");

        // ==========================
        // 【测试点 2：读取文件前测试】
        // ==========================
        System.out.println("【测试】开始读取文件：" + JOB_FILE_PATH);

        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);

        List<Job> newJobList = new ArrayList<>(jobList);
        newJobList.add(newJob);
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, newJobList);

        System.out.println("【测试】发布成功！");
        return newJob;
    }

    // ========= 录用申请者 =========
    public Application acceptApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            return null;
        }

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);

        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) {
            return null;
        }

        Job targetJob = null;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                break;
            }
        }
        if (targetJob == null || !moId.equals(targetJob.getMoId())) {
            return null;
        }

        if (!"PENDING".equals(targetApp.getAppStatus())) {
            return null;
        }

        List<Application> newAppList = new ArrayList<>(appList);
        for (Application app : newAppList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("ACCEPTED");
                targetApp = app;
                break;
            }
        }
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, newAppList);

        List<Job> newJobList = new ArrayList<>(jobList);
        for (Job job : newJobList) {
            if (targetJob.getJobId().equals(job.getJobId())) {
                job.setJobStatus("FILLED");
                break;
            }
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, newJobList);

        return targetApp;
    }

    // ======================
    // 取消录用
    // ======================
    public boolean cancelApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            return false;
        }

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);

        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && "ACCEPTED".equals(app.getAppStatus())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) {
            return false;
        }

        Job targetJob = null;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                break;
            }
        }
        if (targetJob == null || !moId.equals(targetJob.getMoId())) {
            return false;
        }

        for (Application app : appList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("PENDING");
                break;
            }
        }
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        for (Job job : jobList) {
            if (targetJob.getJobId().equals(job.getJobId())) {
                job.setJobStatus("OPEN");
                break;
            }
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);

        return true;
    }

    // ========== 获取所有岗位 ==========
    public List<Job> getAllJobs() {
        return CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
    }

    // ========== 查看所有申请 ==========
    public List<Application> getAllApps(String userId, String password, boolean isAdmin) {
        if (!AuthUtil.authenticate(userId, password, isAdmin)) {
            return null;
        }
        return CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
    }

    // ========== 生成随机码 ==========
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