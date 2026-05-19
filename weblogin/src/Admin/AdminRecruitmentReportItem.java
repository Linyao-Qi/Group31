package Admin;

public class AdminRecruitmentReportItem {
    private final String jobId;
    private final String moId;
    private final String subject;
    private final String workType;
    private final double hoursPerWeek;
    private final String compensation;
    private final String status;
    private final int applicationCount;
    private final int pendingCount;
    private final int maxHire;
    private final int acceptedCount;
    private final int remainingVacancy;
    private final String recruitmentProgress;

    public AdminRecruitmentReportItem(
            String jobId,
            String moId,
            String subject,
            String workType,
            double hoursPerWeek,
            String compensation,
            String status,
            int applicationCount,
            int pendingCount,
            int maxHire,
            int acceptedCount,
            int remainingVacancy,
            String recruitmentProgress
    ) {
        this.jobId = jobId;
        this.moId = moId;
        this.subject = subject;
        this.workType = workType;
        this.hoursPerWeek = hoursPerWeek;
        this.compensation = compensation;
        this.status = status;
        this.applicationCount = applicationCount;
        this.pendingCount = pendingCount;
        this.maxHire = maxHire;
        this.acceptedCount = acceptedCount;
        this.remainingVacancy = remainingVacancy;
        this.recruitmentProgress = recruitmentProgress;
    }

    public String getJobId() {
        return jobId;
    }

    public String getMoId() {
        return moId;
    }

    public String getSubject() {
        return subject;
    }

    public String getWorkType() {
        return workType;
    }

    public double getHoursPerWeek() {
        return hoursPerWeek;
    }

    public String getCompensation() {
        return compensation;
    }

    public String getStatus() {
        return status;
    }

    public int getApplicationCount() {
        return applicationCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public int getMaxHire() {
        return maxHire;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public int getRemainingVacancy() {
        return remainingVacancy;
    }

    public String getRecruitmentProgress() {
        return recruitmentProgress;
    }
}
