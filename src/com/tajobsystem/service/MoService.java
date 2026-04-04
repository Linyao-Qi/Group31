package com.tajobsystem.service;

import com.tajobsystem.data.ApplicationLoader;
import com.tajobsystem.data.JobDataLoader;
import com.tajobsystem.model.Application;
import com.tajobsystem.model.Job;

import java.util.List;
import java.util.UUID;

public class MoService {
    private static final String JOB_FILE  = "./data/jobs.csv";
    private static final String APP_FILE  = "./data/app.csv";

    public Job publishJob(String moId, String jobName, String jobRequirements) {
        if (moId == null || moId.isBlank() || jobName == null || jobName.isBlank() || jobRequirements == null || jobRequirements.isBlank()) {
            System.err.println("参数不能为空");
            return null;
        }

        String jobId = UUID.randomUUID().toString().replace("-", "");
        Job job = new Job(jobId, jobName, null, null, null, null, jobRequirements, 0, null, null, null, true);
        job.setMoId(moId);
        job.setJobStatus("OPEN");

        List<Job> jobs = JobDataLoader.loadJobsFromCSV(JOB_FILE);
        jobs.add(job);
        JobDataLoader.writeJobsToCSV(JOB_FILE, jobs);

        System.out.println("岗位发布成功：" + job);
        return job;
    }

    public Application acceptApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            System.err.println("参数不能为空");
            return null;
        }

        List<Application> apps = ApplicationLoader.loadApplicationsFromCSV(APP_FILE);

        Application target = null;
        for (Application a : apps) {
            if (appId.equals(a.getAppId())) {
                target = a;
                break;
            }
        }
        if (target == null) {
            System.err.println("未找到申请");
            return null;
        }

        List<Job> allJobs = JobDataLoader.loadJobsFromCSV(JOB_FILE);

        Job job = null;
        for (Job j : allJobs) {
            if (target.getJobId().equals(j.getJobId())) {
                job = j;
                break;
            }
        }
        if (job == null) {
            System.err.println("未找到对应岗位");
            return null;
        }

        if (!moId.equals(job.getMoId())) {
            System.err.println("无权限");
            return null;
        }

        if (!"Submitted".equals(target.getAppStatus())) {
            System.err.println("只能处理待审核申请");
            return null;
        }

        target.setAppStatus("Accepted");
        ApplicationLoader.writeApplicationsToCSV(APP_FILE, apps);
        System.out.println("录用成功：" + target);
        return target;
    }
}
