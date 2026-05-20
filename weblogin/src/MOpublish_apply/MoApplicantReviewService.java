package com;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class for MO applicant review operations.
 * <p>Loads applications, published jobs, TA profile skills, and job skill
 * requirements so the applicant review servlet can display matching data.</p>
 *
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class MoApplicantReviewService {

    /** File path for job data CSV */
    private static String JOB_FILE_PATH = "data/job.csv";

    /** File path for TA profile CSV */
    private static String TA_CSV_PATH = "data/profiles.csv";

    /** File path for application data CSV */
    private static String APP_FILE_PATH = "data/application.csv";

    /** Cached TA skill map keyed by TA ID */
    private static Map<String, String> TA_SKILL_MAP;

    /**
     * Initialize CSV paths and load TA skills from servlet context.
     *
     * @param context ServletContext used to resolve real file paths
     */
    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        TA_CSV_PATH = context.getRealPath("data/profiles.csv");
        APP_FILE_PATH = context.getRealPath("data/application.csv");
        
        TA_SKILL_MAP = TaCsvUtil.loadTaSkills(TA_CSV_PATH);
               
    }

    /**
     * Validate MO credentials.
     *
     * @param moId MO user ID
     * @param password MO password
     * @return true if the MO credentials are valid
     */
    public boolean isValidMO(String moId, String password) {
        return AuthUtil.authenticateMO(moId, password);
    }

    /**
     * Get applications for jobs published by a specific MO.
     *
     * @param moId MO user ID
     * @return applications associated with the MO's published jobs
     */
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

    /**
     * Get skills from the TA profile cache by TA ID.
     *
     * @param taId TA user ID
     * @return TA skill string, or empty string if no profile skills are found
     */
    public String getTaSkill(String taId) {
        if (TA_SKILL_MAP == null) return "";
        return TA_SKILL_MAP.getOrDefault(taId, "");
    }

    /**
     * Get skills for an application, preferring profile skills over application skills.
     *
     * @param app application being reviewed
     * @return TA skill string used for matching
     */
    public String getTaSkill(Application app) {
        String profileSkills = getTaSkill(app.getTaId());
        if (profileSkills != null && !profileSkills.isBlank()) {
            return profileSkills;
        }
        return app.getSkills() == null ? "" : app.getSkills();
    }

    /**
     * Get skill requirements for a job by job ID.
     *
     * @param jobId job identifier
     * @return required skill string, or empty string if the job is not found
     */
    public String getSkillRequirement(String jobId) {
        List<Job> jobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        for (Job job : jobs) {
            if (jobId.equals(job.getJobId())) {
                return job.getSkillRequirement();
            }
        }
        return "";
    }

    /**
     * Get all jobs published by a specific MO.
     *
     * @param moId MO user ID
     * @return list of jobs owned by the MO
     */
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
