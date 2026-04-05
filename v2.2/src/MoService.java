package com;

import java.util.ArrayList;
import java.util.List;

public class MoService {
    // 替换为CSV文件路径
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String JOB_FILE_PATH = PROJECT_PATH + "/data/job.csv";
    private static final String APP_FILE_PATH = PROJECT_PATH + "/data/application.csv";

    // 发布岗位（适配CSV + jobId = moId + 6位随机码）
    public Job publishJob(String moId, String jobName, String jobRequirements) {
        if (moId == null || moId.isBlank() || jobName == null || jobName.isBlank()
                || jobRequirements == null || jobRequirements.isBlank()) {
            return null;
        }
        
        // 生成 6 位随机字母+数字
        String randomCode = generateRandomCode(6);
        // 拼接最终 jobId
        String jobId = moId + randomCode;

        Job newJob = new Job(jobId, moId, jobName, jobRequirements, "OPEN");
        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        List<Job> newJobList = new ArrayList<>(jobList);
        newJobList.add(newJob);
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, newJobList);
        return newJob;
    }

    // 录用申请者（适配CSV）
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

        List<Application> newAppList = new ArrayList<>(appList);
        for (Application app : newAppList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("ACCEPTED");
                targetApp = app;
                break;
            }
        }
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, newAppList);
        return targetApp;
    }

    // 获取所有岗位（适配CSV）
    public List<Job> getAllJobs() {
        return CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
    }

    // 获取所有申请（适配CSV）
    public List<Application> getAllApps() {
        return CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
    }
    
    // 生成指定位数随机字母+数字
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