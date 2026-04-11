package com;

import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletContext;
public class MoService {

	 // 路径：Web 应用根目录下的 data/xxx.csv
    private static String JOB_FILE_PATH = "data/job.csv";
    private static String APP_FILE_PATH = "data/application.csv";

    // ====================== 【完全仿照 AuthUtil 的 init 方法】 ======================
    public static void init(ServletContext context) {
        // 👇 和 AuthUtil 完全一样！！！
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        APP_FILE_PATH = context.getRealPath("data/application.csv");
    }
    // ========== 发布岗位（需 MO 身份认证） ==========
    public Job publishJob(String moId, String password, String jobName, String jobRequirements,String skillRequirement) {
        // MO 身份认证
        if (!AuthUtil.authenticateMO(moId, password)) {
            return null;
        }

        // 参数非空校验
        if (moId == null || moId.isBlank()
                || jobName == null || jobName.isBlank()
                || jobRequirements == null || jobRequirements.isBlank()) {
            return null;
        }

        // 生成 6 位随机码
        String randomCode = generateRandomCode(6);
        String jobId = moId + randomCode;

        Job newJob = new Job(jobId, moId, jobName, jobRequirements, "OPEN",skillRequirement);
        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);

        List<Job> newJobList = new ArrayList<>(jobList);
        newJobList.add(newJob);
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, newJobList);

        return newJob;
    }

    // ========= 录用申请者 =========
    public Application acceptApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            return null;
        }

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);

        // 查找目标申请
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

        // 查找对应岗位并校验权限
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

        // 校验状态
        if (!"PENDING".equals(targetApp.getAppStatus())) {
            return null;
        }

        // =======修改申请者状态为 ACCEPTED ==============
        List<Application> newAppList = new ArrayList<>(appList);
        for (Application app : newAppList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("ACCEPTED");
                targetApp = app;
                break;
            }
        }
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, newAppList);

        // ======= 将岗位状态改为 FILLED（已招满） =============
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

        // 校验岗位权限
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

        // 恢复申请状态为 PENDING
        for (Application app : appList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("PENDING");
                break;
            }
        }
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        // 恢复岗位为 OPEN
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

    // ========== 查看所有申请（需 MO / Admin 认证） ==========
    public List<Application> getAllApps(String userId, String password, boolean isAdmin) {
        // 身份认证
        if (!AuthUtil.authenticate(userId, password, isAdmin)) {
            return null;
        }
        return CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
    }

    // ========== 生成指定位数随机字母 + 数字 ==========
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