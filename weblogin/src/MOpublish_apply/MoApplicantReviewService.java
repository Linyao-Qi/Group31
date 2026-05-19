package com;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoApplicantReviewService {

    
    private static String JOB_FILE_PATH = "data/job.csv";
    private static String TA_CSV_PATH = "data/profiles.csv";
    private static String APP_FILE_PATH = "data/application.csv";
    private static Map<String, String> TA_SKILL_MAP;

    
    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        TA_CSV_PATH = context.getRealPath("data/profiles.csv");
        APP_FILE_PATH = context.getRealPath("data/application.csv");
        
        TA_SKILL_MAP = TaCsvUtil.loadTaSkills(TA_CSV_PATH);
               
    }

    
    public boolean isValidMO(String moId, String password) {
        return AuthUtil.authenticateMO(moId, password);
    }

    public List<Application> getApplications(String moId) {
        List<Application> realApps = new ArrayList<>();
        List<Application> allApps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> moJobs = getMoPublishedJobs(moId);

        if (moJobs.isEmpty() || allApps.isEmpty()) {
            return realApps;
        }

        for (Application app : allApps) {
            if (!moId.equals(app.getMoId())) {
                continue;
            }
            for (Job job : moJobs) {
                if (job.getJobId().equals(app.getJobId())) {
                    realApps.add(app);
                    break;
                }
            }
        }
        
        return realApps;
    }

    public String getTaSkill(String taId) {
        if (TA_SKILL_MAP == null) return "";
        return TA_SKILL_MAP.getOrDefault(taId, "");
    }

    public String getTaSkill(Application app) {
        String profileSkills = getTaSkill(app.getTaId());
        if (profileSkills != null && !profileSkills.isBlank()) {
            return profileSkills;
        }
        return app.getSkills() == null ? "" : app.getSkills();
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
