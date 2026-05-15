package Admin;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminRecruitmentReportService {
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final AdminDataManagement dataManagement = new AdminDataManagement();

    public ReportData buildReport() throws IOException {
        List<AdminRecruitment> jobs = dataManagement.loadPosts();
        List<AdminApplicationRecord> applications = dataManagement.loadApplications();

        Map<String, JobApplicationCounter> countersByJobId = new HashMap<>();
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        for (AdminApplicationRecord application : applications) {
            String jobId = safe(application.getJobId());
            String appStatus = normalizeStatus(application.getAppStatus());

            if (!jobId.isEmpty()) {
                JobApplicationCounter counter = countersByJobId.computeIfAbsent(jobId, key -> new JobApplicationCounter());
                counter.total++;
                if ("PENDING".equals(appStatus)) {
                    counter.pending++;
                }
            }

            if (!appStatus.isEmpty()) {
                statusCounts.put(appStatus, statusCounts.getOrDefault(appStatus, 0) + 1);
            }
        }

        Summary summary = buildSummary(jobs, applications, statusCounts);
        List<AdminRecruitmentReportItem> items = buildJobItems(jobs, countersByJobId);
        KeyFindings keyFindings = buildKeyFindings(items, jobs);
        List<ApplicationStatusCount> applicationStatusCounts = buildStatusCounts(statusCounts);
        List<SuccessfulRecruitmentDetail> successfulRecruitmentDetails =
                buildSuccessfulRecruitmentDetails(jobs, applications);

        return new ReportData(
                summary,
                items,
                applicationStatusCounts,
                keyFindings,
                successfulRecruitmentDetails,
                DISPLAY_TIME_FORMAT.format(LocalDateTime.now())
        );
    }

    public String buildCsv(ReportData reportData) {
        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("Recruitment Statistics Report").append('\n');
        csv.append("Generated At,").append(escapeCsv(reportData.getGeneratedAt())).append('\n');
        csv.append('\n');
        csv.append("job ID,MO ID,Subject,Work Type,Hours/Week,Compensation,Status,Application Count,Pending Count")
                .append('\n');
        for (AdminRecruitmentReportItem item : reportData.getItems()) {
            csv.append(escapeCsv(item.getJobId())).append(',')
                    .append(escapeCsv(item.getMoId())).append(',')
                    .append(escapeCsv(item.getSubject())).append(',')
                    .append(escapeCsv(item.getWorkType())).append(',')
                    .append(formatNumber(item.getHoursPerWeek())).append(',')
                    .append(escapeCsv(item.getCompensation())).append(',')
                    .append(escapeCsv(item.getStatus())).append(',')
                    .append(item.getApplicationCount()).append(',')
                    .append(item.getPendingCount())
                    .append('\n');
        }
        return csv.toString();
    }

    public String buildSuccessfulRecruitmentCsv(ReportData reportData) {
        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("job ID,Subject,MO ID,appId,taId,name,email,appStatus").append('\n');
        for (SuccessfulRecruitmentDetail detail : reportData.getSuccessfulRecruitmentDetails()) {
            csv.append(escapeCsv(detail.getJobId())).append(',')
                    .append(escapeCsv(detail.getSubject())).append(',')
                    .append(escapeCsv(detail.getMoId())).append(',')
                    .append(escapeCsv(detail.getAppId())).append(',')
                    .append(escapeCsv(detail.getTaId())).append(',')
                    .append(escapeCsv(detail.getName())).append(',')
                    .append(escapeCsv(detail.getEmail())).append(',')
                    .append(escapeCsv(detail.getAppStatus()))
                    .append('\n');
        }
        return csv.toString();
    }

    private Summary buildSummary(
            List<AdminRecruitment> jobs,
            List<AdminApplicationRecord> applications,
            Map<String, Integer> statusCounts
    ) {
        int openJobs = 0;
        for (AdminRecruitment job : jobs) {
            if (job.isOpen()) {
                openJobs++;
            }
        }

        Summary summary = new Summary();
        summary.totalJobs = jobs.size();
        summary.openJobs = openJobs;
        summary.closedJobs = jobs.size() - openJobs;
        summary.totalApplications = applications.size();
        summary.pendingApplications = statusCounts.getOrDefault("PENDING", 0);
        summary.rejectedApplications = statusCounts.getOrDefault("REJECTED", 0);
        summary.canceledApplications = statusCounts.getOrDefault("CANCELED", 0)
                + statusCounts.getOrDefault("CANCELLED", 0);
        summary.successfulApplications = statusCounts.getOrDefault("ACCEPTED", 0)
                + statusCounts.getOrDefault("SUCCESSFUL", 0)
                + statusCounts.getOrDefault("HIRED", 0);
        return summary;
    }

    private List<AdminRecruitmentReportItem> buildJobItems(
            List<AdminRecruitment> jobs,
            Map<String, JobApplicationCounter> countersByJobId
    ) {
        List<AdminRecruitmentReportItem> items = new ArrayList<>();
        for (AdminRecruitment job : jobs) {
            JobApplicationCounter counter = countersByJobId.getOrDefault(safe(job.getJobId()), new JobApplicationCounter());
            items.add(new AdminRecruitmentReportItem(
                    safe(job.getJobId()),
                    safe(job.getMoId()),
                    safe(job.getSubject()),
                    safe(job.getWorkType()),
                    job.getHoursPerWeek(),
                    safe(job.getCompensation()),
                    job.isOpen() ? "OPEN" : "CLOSED",
                    counter.total,
                    counter.pending
            ));
        }

        items.sort(Comparator.comparingInt(AdminRecruitmentReportItem::getApplicationCount).reversed()
                .thenComparing(AdminRecruitmentReportItem::getJobId));
        return items;
    }

    private List<ApplicationStatusCount> buildStatusCounts(Map<String, Integer> statusCounts) {
        List<ApplicationStatusCount> counts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            counts.add(new ApplicationStatusCount(entry.getKey(), entry.getValue()));
        }
        counts.sort(Comparator.comparing(ApplicationStatusCount::getAppStatus));
        return counts;
    }

    private List<SuccessfulRecruitmentDetail> buildSuccessfulRecruitmentDetails(
            List<AdminRecruitment> jobs,
            List<AdminApplicationRecord> applications
    ) {
        Map<String, AdminRecruitment> jobsById = new HashMap<>();
        for (AdminRecruitment job : jobs) {
            jobsById.put(safe(job.getJobId()), job);
        }

        List<SuccessfulRecruitmentDetail> details = new ArrayList<>();
        for (AdminApplicationRecord application : applications) {
            String appStatus = normalizeStatus(application.getAppStatus());
            if (!isSuccessfulStatus(appStatus)) {
                continue;
            }

            AdminRecruitment job = jobsById.get(safe(application.getJobId()));
            String subject = job == null ? "" : safe(job.getSubject());
            String moId = job == null ? safe(application.getMoId()) : safe(job.getMoId());
            details.add(new SuccessfulRecruitmentDetail(
                    safe(application.getJobId()),
                    subject,
                    moId,
                    safe(application.getAppId()),
                    safe(application.getTaId()),
                    safe(application.getName()),
                    safe(application.getEmail()),
                    appStatus
            ));
        }

        details.sort(Comparator.comparing(SuccessfulRecruitmentDetail::getJobId)
                .thenComparing(SuccessfulRecruitmentDetail::getAppId));
        return details;
    }

    private boolean isSuccessfulStatus(String appStatus) {
        return "ACCEPTED".equals(appStatus)
                || "SUCCESSFUL".equals(appStatus)
                || "HIRED".equals(appStatus);
    }

    private KeyFindings buildKeyFindings(List<AdminRecruitmentReportItem> items, List<AdminRecruitment> jobs) {
        int jobsWithNoApplications = 0;
        AdminRecruitmentReportItem mostAppliedJob = null;
        for (AdminRecruitmentReportItem item : items) {
            if (item.getApplicationCount() == 0) {
                jobsWithNoApplications++;
            }
            if (mostAppliedJob == null || item.getApplicationCount() > mostAppliedJob.getApplicationCount()) {
                mostAppliedJob = item;
            }
        }

        Map<String, Integer> jobCountByMoId = new HashMap<>();
        for (AdminRecruitment job : jobs) {
            String moId = safe(job.getMoId());
            if (!moId.isEmpty()) {
                jobCountByMoId.put(moId, jobCountByMoId.getOrDefault(moId, 0) + 1);
            }
        }

        String moIdWithMostJobs = "";
        int mostJobsByMo = 0;
        for (Map.Entry<String, Integer> entry : jobCountByMoId.entrySet()) {
            if (entry.getValue() > mostJobsByMo) {
                moIdWithMostJobs = entry.getKey();
                mostJobsByMo = entry.getValue();
            }
        }

        String mostAppliedJobLabel = "N/A";
        int mostAppliedJobCount = 0;
        if (mostAppliedJob != null) {
            mostAppliedJobLabel = mostAppliedJob.getSubject().isEmpty()
                    ? mostAppliedJob.getJobId()
                    : mostAppliedJob.getSubject() + " (" + mostAppliedJob.getJobId() + ")";
            mostAppliedJobCount = mostAppliedJob.getApplicationCount();
        }

        return new KeyFindings(
                jobsWithNoApplications,
                mostAppliedJobLabel,
                mostAppliedJobCount,
                moIdWithMostJobs,
                mostJobsByMo
        );
    }

    private String normalizeStatus(String status) {
        return safe(status).toUpperCase();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeCsv(String value) {
        String safeValue = value == null ? "" : value;
        if (safeValue.contains(",") || safeValue.contains("\"") || safeValue.contains("\n") || safeValue.contains("\r")) {
            return "\"" + safeValue.replace("\"", "\"\"") + "\"";
        }
        return safeValue;
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private static class JobApplicationCounter {
        int total;
        int pending;
    }

    public static class Summary {
        private int totalJobs;
        private int openJobs;
        private int closedJobs;
        private int totalApplications;
        private int pendingApplications;
        private int rejectedApplications;
        private int canceledApplications;
        private int successfulApplications;

        public int getTotalJobs() {
            return totalJobs;
        }

        public int getOpenJobs() {
            return openJobs;
        }

        public int getClosedJobs() {
            return closedJobs;
        }

        public int getTotalApplications() {
            return totalApplications;
        }

        public int getPendingApplications() {
            return pendingApplications;
        }

        public int getRejectedApplications() {
            return rejectedApplications;
        }

        public int getCanceledApplications() {
            return canceledApplications;
        }

        public int getSuccessfulApplications() {
            return successfulApplications;
        }
    }

    public static class ApplicationStatusCount {
        private final String appStatus;
        private final int count;

        public ApplicationStatusCount(String appStatus, int count) {
            this.appStatus = appStatus;
            this.count = count;
        }

        public String getAppStatus() {
            return appStatus;
        }

        public int getCount() {
            return count;
        }
    }

    public static class KeyFindings {
        private final int jobsWithNoApplications;
        private final String mostAppliedJob;
        private final int mostAppliedJobApplicationCount;
        private final String moIdWithMostJobs;
        private final int moJobCount;

        public KeyFindings(
                int jobsWithNoApplications,
                String mostAppliedJob,
                int mostAppliedJobApplicationCount,
                String moIdWithMostJobs,
                int moJobCount
        ) {
            this.jobsWithNoApplications = jobsWithNoApplications;
            this.mostAppliedJob = mostAppliedJob;
            this.mostAppliedJobApplicationCount = mostAppliedJobApplicationCount;
            this.moIdWithMostJobs = moIdWithMostJobs;
            this.moJobCount = moJobCount;
        }

        public int getJobsWithNoApplications() {
            return jobsWithNoApplications;
        }

        public String getMostAppliedJob() {
            return mostAppliedJob;
        }

        public int getMostAppliedJobApplicationCount() {
            return mostAppliedJobApplicationCount;
        }

        public String getMoIdWithMostJobs() {
            return moIdWithMostJobs;
        }

        public int getMoJobCount() {
            return moJobCount;
        }
    }

    public static class SuccessfulRecruitmentDetail {
        private final String jobId;
        private final String subject;
        private final String moId;
        private final String appId;
        private final String taId;
        private final String name;
        private final String email;
        private final String appStatus;

        public SuccessfulRecruitmentDetail(
                String jobId,
                String subject,
                String moId,
                String appId,
                String taId,
                String name,
                String email,
                String appStatus
        ) {
            this.jobId = jobId;
            this.subject = subject;
            this.moId = moId;
            this.appId = appId;
            this.taId = taId;
            this.name = name;
            this.email = email;
            this.appStatus = appStatus;
        }

        public String getJobId() {
            return jobId;
        }

        public String getSubject() {
            return subject;
        }

        public String getMoId() {
            return moId;
        }

        public String getAppId() {
            return appId;
        }

        public String getTaId() {
            return taId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getAppStatus() {
            return appStatus;
        }
    }

    public static class ReportData {
        private final Summary summary;
        private final List<AdminRecruitmentReportItem> items;
        private final List<ApplicationStatusCount> applicationStatusCounts;
        private final KeyFindings keyFindings;
        private final List<SuccessfulRecruitmentDetail> successfulRecruitmentDetails;
        private final String generatedAt;

        public ReportData(
                Summary summary,
                List<AdminRecruitmentReportItem> items,
                List<ApplicationStatusCount> applicationStatusCounts,
                KeyFindings keyFindings,
                List<SuccessfulRecruitmentDetail> successfulRecruitmentDetails,
                String generatedAt
        ) {
            this.summary = summary;
            this.items = items;
            this.applicationStatusCounts = applicationStatusCounts;
            this.keyFindings = keyFindings;
            this.successfulRecruitmentDetails = successfulRecruitmentDetails;
            this.generatedAt = generatedAt;
        }

        public Summary getSummary() {
            return summary;
        }

        public List<AdminRecruitmentReportItem> getItems() {
            return items;
        }

        public List<ApplicationStatusCount> getApplicationStatusCounts() {
            return applicationStatusCounts;
        }

        public KeyFindings getKeyFindings() {
            return keyFindings;
        }

        public List<SuccessfulRecruitmentDetail> getSuccessfulRecruitmentDetails() {
            return successfulRecruitmentDetails;
        }

        public String getGeneratedAt() {
            return generatedAt;
        }
    }
}
