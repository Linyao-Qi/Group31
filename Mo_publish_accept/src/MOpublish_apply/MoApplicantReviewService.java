package com;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoApplicantReviewService {

    
    private static String JOB_FILE_PATH = "data/job.csv";
    private static String TA_CSV_PATH = "data/profiles.csv";
    private static Map<String, String> TA_SKILL_MAP;
    private static List<String> ALL_TA_IDS;

    
    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        TA_CSV_PATH = context.getRealPath("data/profiles.csv");
        
        
        TA_SKILL_MAP = TaCsvUtil.loadTaSkills(TA_CSV_PATH);
        ALL_TA_IDS = TaCsvUtil.getAllTaIds(TA_CSV_PATH);
               
    }

    
    public boolean isValidMO(String moId, String password) {
        return AuthUtil.authenticateMO(moId, password);
    }

    public List<Application> getApplications(String moId) {
        List<Application> realApps = new ArrayList<>();

        List<Job> moJobs = getMoPublishedJobs(moId);

        if (moJobs.isEmpty() || ALL_TA_IDS == null || ALL_TA_IDS.isEmpty()) {
            return realApps;
        }

        for (Job job : moJobs) {
            for (String taId : ALL_TA_IDS) {
                Application app = new Application();
                app.setAppId("APP_" + UUID.randomUUID().toString().substring(0, 8));
                app.setJobId(job.getJobId());
                app.setTaId(taId);
                app.setAppStatus("PENDING");
                realApps.add(app);
            }
        }
        
        return realApps;
    }

    public String getTaSkill(String taId) {
        if (TA_SKILL_MAP == null) return "";
        return TA_SKILL_MAP.getOrDefault(taId, "");
    }

    public String getSkillRequirement(String jobId) {
        List<Job> jobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        for (Job job : jobs) {
            if (jobId.equals(job.getJobId())) {
                return job.getSkillRequirement();
            }
        }
        return "";
    }

    private List<Job> getMoPublishedJobs(String moId) {
        List<Job> allJobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        List<Job> moJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (moId.equals(job.getMoId())) {
                moJobs.add(job);
            }
        }
        return moJobs;
    }
}