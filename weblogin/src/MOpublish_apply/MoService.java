package com;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Business Service Class for Module Organizer (MO)
 * <p>Core service layer for MO operations including job publishing,
 * application review, hiring, rejection, status management, and data queries.
 * Integrates with CSV utilities and authentication for secure business logic processing.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class MoService {

    /** File path for job data CSV */
    public static String JOB_FILE_PATH;

    /** File path for application data CSV */
    public static String APP_FILE_PATH;

    /**
     * Initialize CSV file paths using servlet context
     * @param context ServletContext for retrieving real file paths
     */
    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        APP_FILE_PATH = context.getRealPath("data/application.csv");
    }

    /**
     * Retrieve all available job positions
     * @return list of Job entities
     */
    public List<Job> getAllJobs() {
        if (JOB_FILE_PATH == null || JOB_FILE_PATH.isEmpty()) {
            return new ArrayList<>();
        }
        return CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
    }

    /**
     * Publish a new TA position with max hire limit
     * @param moId MO user ID
     * @param password MO password
     * @param subject course name
     * @param workType type of work
     * @param description job details
     * @param skillRequirement required skills
     * @param hoursPerWeek weekly working hours
     * @param compensation salary info
     * @param maxHire maximum allowed hires
     * @return created Job object or null if failed
     */
    public Job publishJob(String moId, String password, String subject, String workType,
                          String description, String skillRequirement, int hoursPerWeek,
                          String compensation, int maxHire) {
        if (JOB_FILE_PATH == null) return null;
        if (!AuthUtil.authenticateMO(moId, password)) return null;
        return publishJobForMo(moId, subject, workType, description, skillRequirement,
                hoursPerWeek, compensation, maxHire);
    }

    /**
     * Internal method to create and save a new job
     * @param moId MO user ID
     * @param subject course name
     * @param workType work type
     * @param description job description
     * @param skillRequirement required skills
     * @param hoursPerWeek weekly hours
     * @param compensation salary
     * @param maxHire max hiring quota
     * @return created Job or null
     */
    public Job publishJobForMo(String moId, String subject, String workType,
                               String description, String skillRequirement, int hoursPerWeek,
                               String compensation, int maxHire) {
        if (JOB_FILE_PATH == null) return null;
        if (moId == null || moId.isBlank() || subject == null || subject.isBlank()
                || workType == null || workType.isBlank() || description == null || description.isBlank()
                || skillRequirement == null || skillRequirement.isBlank() || compensation == null || compensation.isBlank()
                || maxHire <= 0) {
            return null;
        }

        String randomCode = generateRandomCode(6);
        String jobId = moId + randomCode;

        Job newJob = new Job(
                jobId, moId, subject, workType, description,
                skillRequirement, hoursPerWeek, compensation, "OPEN", maxHire
        );

        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        jobList.add(newJob);
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);
        return newJob;
    }

    /**
     * Overloaded publish method with default maxHire = 1
     */
    public Job publishJob(String moId, String password, String subject, String workType,
                          String description, String skillRequirement, int hoursPerWeek, String compensation) {
        return publishJob(moId, password, subject, workType, description, skillRequirement,
                hoursPerWeek, compensation, 1);
    }

    /**
     * Accept and hire a qualified applicant
     * Checks max hire limit before approval
     * @param moId MO user ID
     * @param appId application ID
     * @return updated Application or null if failed
     */
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

        Job targetJob = null;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                break;
            }
        }
        if (targetJob == null) return null;

        int acceptedCount = 0;
        for (Application app : appList) {
            if (targetApp.getJobId().equals(app.getJobId())
                    && "ACCEPTED".equals(app.getAppStatus())) {
                acceptedCount++;
            }
        }

        if (acceptedCount >= targetJob.getMaxHire()) {
            return null;
        }

        targetApp.setAppStatus("ACCEPTED");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        if (acceptedCount + 1 >= targetJob.getMaxHire()) {
            targetJob.setStatus("FILLED");
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);

        return targetApp;
    }

    /**
     * Cancel a previously accepted application
     * Updates job status back to OPEN if applicable
     * @param moId MO user ID
     * @param cancelAppId application ID
     * @return true if successful
     */
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

        Job targetJob = null;
        int acceptedCount = 0;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                for (Application app : appList) {
                    if (job.getJobId().equals(app.getJobId())
                            && "ACCEPTED".equals(app.getAppStatus())) {
                        acceptedCount++;
                    }
                }
                break;
            }
        }

        if (targetJob != null && acceptedCount < targetJob.getMaxHire()) {
            targetJob.setStatus("OPEN");
            CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);
        }

        return true;
    }

    /**
     * Reject a pending application
     * @param moId MO user ID
     * @param appId application ID
     * @return updated Application or null
     */
    public Application rejectApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) return null;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);

        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return null;
        if (!"PENDING".equals(targetApp.getAppStatus())) return null;

        targetApp.setAppStatus("REJECTED");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        return targetApp;
    }

    /**
     * Cancel rejection and restore application to PENDING status
     * @param moId MO user ID
     * @param appId application ID
     * @return updated Application or null
     */
    public Application cancelRejectApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) return null;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);

        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return null;
        if (!"REJECTED".equals(targetApp.getAppStatus())) return null;

        targetApp.setAppStatus("PENDING");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        return targetApp;
    }

    /**
     * Get all applications belonging to the current MO
     * @param moId MO user ID
     * @param password MO password
     * @param isAdmin admin flag
     * @return list of applications
     */
    public List<Application> getAllApps(String moId, String password, boolean isAdmin) {
        if (!AuthUtil.authenticateMO(moId, password)) return null;
        return getAllAppsForMo(moId);
    }

    /**
     * Internal method to filter applications by MO ID
     * @param moId MO user ID
     * @return filtered application list
     */
    public List<Application> getAllAppsForMo(String moId) {
        if (moId == null || moId.isBlank()) return new ArrayList<>();

        List<Application> allApps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Application> result = new ArrayList<>();
        for (Application app : allApps) {
            if (moId.equals(app.getMoId())) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Generate a random alphanumeric code for job ID
     * @param length code length
     * @return random string
     */
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
