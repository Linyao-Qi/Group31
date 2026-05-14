package TaJob;

public class JobPosting {
    private final String jobId;
    private final String moId;
    private final String subject;
    private final String workType;
    private final String description;
    private final String skillRequirement;
    private final String hoursPerWeek;
    private final String compensation;
    private final String status;

    public JobPosting(
            String jobId,
            String moId,
            String subject,
            String workType,
            String description,
            String skillRequirement,
            String hoursPerWeek,
            String compensation,
            String status) {
        this.jobId = nullToEmpty(jobId);
        this.moId = nullToEmpty(moId);
        this.subject = nullToEmpty(subject);
        this.workType = nullToEmpty(workType);
        this.description = nullToEmpty(description);
        this.skillRequirement = nullToEmpty(skillRequirement);
        this.hoursPerWeek = nullToEmpty(hoursPerWeek);
        this.compensation = nullToEmpty(compensation);
        this.status = nullToEmpty(status);
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

    public String getDescription() {
        return description;
    }

    public String getSkillRequirement() {
        return skillRequirement;
    }

    public String getHoursPerWeek() {
        return hoursPerWeek;
    }

    public String getCompensation() {
        return compensation;
    }

    public String getStatus() {
        return status;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
