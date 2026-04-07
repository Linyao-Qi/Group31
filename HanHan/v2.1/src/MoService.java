package com;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoService {
    // Web工程适配路径，项目根目录下的data文件夹，运行时自动生成
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String JOB_FILE_PATH = PROJECT_PATH + "/data/job.json";
    private static final String APP_FILE_PATH = PROJECT_PATH + "/data/application.json";

    // 发布岗位
    public Job publishJob(String moId, String jobName, String jobRequirements) {
        if (moId == null || moId.isBlank() || jobName == null || jobName.isBlank()
                || jobRequirements == null || jobRequirements.isBlank()) {
            return null;
        }
        String jobId = UUID.randomUUID().toString().replace("-", "");
        Job newJob = new Job(jobId, moId, jobName, jobRequirements, "OPEN");
        List<Job> jobList = JsonFileUtil.readListFromJson(JOB_FILE_PATH, new TypeToken<List<Job>>() {});
        List<Job> newJobList = new ArrayList<>(jobList);
        newJobList.add(newJob);
        JsonFileUtil.writeListToJson(JOB_FILE_PATH, newJobList);
        return newJob;
    }

    // 录用申请者（业务逻辑完全不变，含权限校验/状态校验）
    public Application acceptApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            return null;
        }
        List<Application> appList = JsonFileUtil.readListFromJson(APP_FILE_PATH, new TypeToken<List<Application>>() {});
        List<Job> jobList = JsonFileUtil.readListFromJson(JOB_FILE_PATH, new TypeToken<List<Job>>() {});

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
        JsonFileUtil.writeListToJson(APP_FILE_PATH, newAppList);
        return targetApp;
    }

    // 新增：获取所有岗位（供页面展示）
    public List<Job> getAllJobs() {
        return JsonFileUtil.readListFromJson(JOB_FILE_PATH, new TypeToken<List<Job>>() {});
    }

    // 新增：获取所有申请（供页面展示）
    public List<Application> getAllApps() {
        return JsonFileUtil.readListFromJson(APP_FILE_PATH, new TypeToken<List<Application>>() {});
    }
}