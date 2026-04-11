package com.tajobsystem.service;

import com.tajobsystem.data.ApplicationLoader;
import com.tajobsystem.data.TAProfileLoader;
import com.tajobsystem.model.Application;
import com.tajobsystem.model.Job;
import com.tajobsystem.model.TAProfile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ApplicationService {

    private static final String APP_FILE  = "./data/app.csv";
    private static final String PROF_FILE = "./data/profile.csv";

    // ------------------------------------------------------------------ //
    //  Feature 1 – Apply for Job                                          //
    // ------------------------------------------------------------------ //

    /**
     * TA submits an application for a job.
     *
     * @return the created Application, or null if validation fails.
     *         Callers should check the returned object and display
     *         the appropriate message from {@link ApplyResult}.
     */
    /**
     * @param cvFilePath path to the selected CV PDF (relative to project root),
     *                   or null if no CV selected.
     */
    public ApplyResult applyForJob(TAProfile ta, Job job, String cvFilePath) {
        if (ta == null || job == null) {
            return ApplyResult.error("TA profile or job cannot be null.");
        }
        if (!job.isOpen()) {
            return ApplyResult.error("This position is no longer open.");
        }

        List<Application> apps = ApplicationLoader.loadApplicationsFromCSV(APP_FILE);

        // Requirement 3: same TA cannot apply to the same job more than once
        boolean alreadyApplied = apps.stream().anyMatch(a ->
                a.getTaId().equals(ta.getTaId()) &&
                a.getJobId().equals(job.getJobId()) &&
                !a.getAppStatus().equals("Withdrawn")
        );
        if (alreadyApplied) {
            return ApplyResult.duplicate("You have already applied for this position.");
        }

        // Requirement 1, 2 & 4: create application linked to TA profile + CV
        String appId = UUID.randomUUID().toString().replace("-", "");
        Application app = new Application(appId, job.getJobId(), ta.getTaId(), "Submitted", cvFilePath);

        saveProfileIfAbsent(ta);
        apps.add(app);
        ApplicationLoader.writeApplicationsToCSV(APP_FILE, apps);

        return ApplyResult.success(app);
    }

    /**
     * Atomically withdraws the existing active application and submits a new one.
     * Does a single read + single write to avoid file I/O consistency issues.
     */
    public ApplyResult withdrawAndReapply(TAProfile ta, Job job, String cvFilePath) {
        if (ta == null || job == null) {
            return ApplyResult.error("TA profile or job cannot be null.");
        }
        if (!job.isOpen()) {
            return ApplyResult.error("This position is no longer open.");
        }

        List<Application> apps = ApplicationLoader.loadApplicationsFromCSV(APP_FILE);

        Application existing = apps.stream()
                .filter(a -> a.getTaId().equals(ta.getTaId())
                        && a.getJobId().equals(job.getJobId())
                        && !a.getAppStatus().equals("Withdrawn"))
                .findFirst().orElse(null);

        if (existing == null) {
            return ApplyResult.error("No active application found to withdraw.");
        }
        if (!existing.getAppStatus().equals("Submitted")) {
            return ApplyResult.error(
                    "Cannot withdraw: application status is \"" + existing.getAppStatus() + "\".");
        }

        // Rule 3: keep record as "Withdrawn"
        existing.setAppStatus("Withdrawn");

        // Add new application
        String appId = UUID.randomUUID().toString().replace("-", "");
        Application newApp = new Application(appId, job.getJobId(), ta.getTaId(), "Submitted", cvFilePath);
        apps.add(newApp);

        // Single write — no intermediate file state
        ApplicationLoader.writeApplicationsToCSV(APP_FILE, apps);
        saveProfileIfAbsent(ta);

        return ApplyResult.success(newApp);
    }

    // ------------------------------------------------------------------ //
    //  Feature 2 – Withdraw Application                                  //
    // ------------------------------------------------------------------ //

    /**
     * Withdraws the active application for a given TA + job (looks up by taId + jobId).
     * Used by ApplyFrame when the user withdraws from the duplicate dialog.
     */
    public WithdrawResult withdrawActiveApplication(String taId, String jobId) {
        if (taId == null || jobId == null) {
            return WithdrawResult.error("Invalid parameters.");
        }
        List<Application> apps = ApplicationLoader.loadApplicationsFromCSV(APP_FILE);
        Application target = apps.stream()
                .filter(a -> a.getTaId().equals(taId)
                        && a.getJobId().equals(jobId)
                        && !"Withdrawn".equals(a.getAppStatus()))
                .findFirst().orElse(null);
        if (target == null) {
            return WithdrawResult.error("No active application found.");
        }
        if (!target.getAppStatus().equals("Submitted")) {
            return WithdrawResult.error(
                "Cannot withdraw: application status is \"" + target.getAppStatus() + "\".");
        }
        target.setAppStatus("Withdrawn");
        ApplicationLoader.writeApplicationsToCSV(APP_FILE, apps);
        return WithdrawResult.success(target);
    }

    /**
     * TA withdraws an application by appId.
     * Only allowed when status is "Submitted" (not yet decided).
     */
    public WithdrawResult withdrawApplication(String taId, String appId) {
        if (taId == null || appId == null) {
            return WithdrawResult.error("Invalid parameters.");
        }

        List<Application> apps = ApplicationLoader.loadApplicationsFromCSV(APP_FILE);

        Application target = apps.stream()
                .filter(a -> a.getAppId().equals(appId))
                .findFirst().orElse(null);

        if (target == null) {
            return WithdrawResult.error("Application not found.");
        }
        if (!target.getTaId().equals(taId)) {
            return WithdrawResult.error("You do not own this application.");
        }
        // Requirement 1: only withdraw before final decision
        if (!target.getAppStatus().equals("Submitted")) {
            return WithdrawResult.error(
                "Cannot withdraw: application status is \"" + target.getAppStatus() + "\".");
        }

        // Requirement 3: keep record with status "Withdrawn"
        target.setAppStatus("Withdrawn");
        ApplicationLoader.writeApplicationsToCSV(APP_FILE, apps);

        return WithdrawResult.success(target);
    }

    // ------------------------------------------------------------------ //
    //  Feature 3 – Check Application Status  (to be implemented next)    //
    // ------------------------------------------------------------------ //

    /**
     * Returns all applications (including history) for the given TA.
     */
    public List<Application> getApplicationsByTA(String taId) {
        List<Application> all = ApplicationLoader.loadApplicationsFromCSV(APP_FILE);
        return all.stream()
                .filter(a -> a.getTaId().equals(taId))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ //
    //  Internal helpers                                                   //
    // ------------------------------------------------------------------ //

    private void saveProfileIfAbsent(TAProfile ta) {
        List<TAProfile> profiles = TAProfileLoader.loadProfilesFromCSV(PROF_FILE);
        boolean exists = profiles.stream()
                .anyMatch(p -> p.getTaId().equals(ta.getTaId()));
        if (!exists) {
            profiles.add(ta);
            TAProfileLoader.writeProfilesToCSV(PROF_FILE, profiles);
        }
    }

    // ------------------------------------------------------------------ //
    //  Result types (simple value objects to carry outcome + message)     //
    // ------------------------------------------------------------------ //

    public static class ApplyResult {
        public enum Status { SUCCESS, DUPLICATE, ERROR }
        public final Status status;
        public final String message;
        public final Application application;   // non-null only on SUCCESS

        private ApplyResult(Status s, String msg, Application app) {
            this.status = s; this.message = msg; this.application = app;
        }
        public static ApplyResult success(Application app) {
            return new ApplyResult(Status.SUCCESS, "Application submitted successfully.", app);
        }
        public static ApplyResult duplicate(String msg) {
            return new ApplyResult(Status.DUPLICATE, msg, null);
        }
        public static ApplyResult error(String msg) {
            return new ApplyResult(Status.ERROR, msg, null);
        }
        public boolean isSuccess() { return status == Status.SUCCESS; }
    }

    public static class WithdrawResult {
        public enum Status { SUCCESS, ERROR }
        public final Status status;
        public final String message;
        public final Application application;

        private WithdrawResult(Status s, String msg, Application app) {
            this.status = s; this.message = msg; this.application = app;
        }
        public static WithdrawResult success(Application app) {
            return new WithdrawResult(Status.SUCCESS, "Application withdrawn.", app);
        }
        public static WithdrawResult error(String msg) {
            return new WithdrawResult(Status.ERROR, msg, null);
        }
        public boolean isSuccess() { return status == Status.SUCCESS; }
    }
}
