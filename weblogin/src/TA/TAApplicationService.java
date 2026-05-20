package TA;

import com.Application;
import com.CsvFileUtil;
import com.Job;
import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

/**
 * TA Application Service
 * <p>Provides the business logic for teaching assistant job applications.
 * It reads and writes application.csv, prevents duplicate active applications,
 * supports withdrawal of pending applications, and loads job details for TA
 * workflows.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class TAApplicationService {

    /** Path to application.csv */
    private static String APP_FILE_PATH;

    /** Path to job.csv */
    private static String JOB_FILE_PATH;

    /**
     * Initializes CSV file paths from the servlet context.
     * @param context servlet context used to resolve data file locations
     */
    public static void init(ServletContext context) {
        APP_FILE_PATH = context.getRealPath("data/application.csv");
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
    }

    /**
     * Checks whether the TA already has an active application for the job.
     * @param taId teaching assistant identifier
     * @param jobId job identifier
     * @return true when an application exists and is not withdrawn
     */
    public static boolean hasActiveApplication(String taId, String jobId) {
        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : apps) {
            if (taId.equals(app.getTaId()) && jobId.equals(app.getJobId())
                    && !"WITHDRAWN".equals(app.getAppStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the current non-withdrawn application status for a TA and job.
     * @param taId teaching assistant identifier
     * @param jobId job identifier
     * @return application status such as PENDING, ACCEPTED, or REJECTED; null if none exists
     */
    public static String getActiveApplicationStatus(String taId, String jobId) {
        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : apps) {
            if (taId.equals(app.getTaId()) && jobId.equals(app.getJobId())
                    && !"WITHDRAWN".equals(app.getAppStatus())) {
                return app.getAppStatus();
            }
        }
        return null;
    }

    /**
     * Creates a new pending job application for a TA.
     * @param taId teaching assistant identifier
     * @param jobId job identifier
     * @param moId module organizer identifier
     * @param name applicant name
     * @param major applicant major
     * @param intro applicant self-introduction
     * @param skills applicant skill list
     * @param email applicant email address
     * @param cvPath stored CV path
     * @return SUCCESS, DUPLICATE, or ERROR
     */
    public static String applyForJob(String taId, String jobId, String moId,
                                     String name, String major, String intro,
                                     String skills, String email, String cvPath) {
        if (APP_FILE_PATH == null) return "ERROR";
        if (hasActiveApplication(taId, jobId)) return "DUPLICATE";

        String appId = "APP" + System.currentTimeMillis();
        Application app = new Application(
                appId, name, jobId, moId, taId,
                major, intro, skills, email,
                cvPath == null ? "" : cvPath,
                "PENDING"
        );

        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        apps.add(app);
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, apps);
        return "SUCCESS";
    }

    /**
     * Withdraws a TA application when it is still pending.
     * @param taId teaching assistant identifier
     * @param appId application identifier
     * @return true when the pending application was removed, false otherwise
     */
    public static boolean withdrawApplication(String taId, String appId) {
        if (APP_FILE_PATH == null) return false;
        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : apps) {
            if (appId.equals(app.getAppId()) && taId.equals(app.getTaId())) {
                if (!"PENDING".equals(app.getAppStatus())) return false;
                apps.remove(app);
                CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, apps);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all applications submitted by a specific TA.
     * @param taId teaching assistant identifier
     * @return list of matching applications
     */
    public static List<Application> getApplicationsByTA(String taId) {
        List<Application> result = new ArrayList<>();
        if (APP_FILE_PATH == null) return result;
        List<Application> allApps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : allApps) {
            if (taId.equals(app.getTaId())) result.add(app);
        }
        return result;
    }

    /**
     * Finds a job by its identifier.
     * @param jobId job identifier
     * @return matching job, or null if it cannot be found
     */
    public static Job getJobById(String jobId) {
        if (JOB_FILE_PATH == null) return null;
        List<Job> jobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        for (Job job : jobs) {
            if (jobId.equals(job.getJobId())) return job;
        }
        return null;
    }
}
