package TaJob;

/**
 * Immutable data model representing one TA job posting loaded from job.csv.
 *
 * @author Linyao Qi
 * @version 3
 */
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

    /**
     * Creates a job posting model. Null field values are normalised to empty strings.
     *
     * @param jobId unique job identifier
     * @param moId module organiser identifier
     * @param subject job subject or module name
     * @param workType type of work, such as Lab or Tutorial
     * @param description job description
     * @param skillRequirement comma-separated skill requirements
     * @param hoursPerWeek expected weekly working hours
     * @param compensation compensation description
     * @param status current job status
     */
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

    /**
     * @return unique job identifier
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return module organiser identifier
     */
    public String getMoId() {
        return moId;
    }

    /**
     * @return job subject or module name
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return job work type
     */
    public String getWorkType() {
        return workType;
    }

    /**
     * @return detailed job description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return comma-separated skill requirements
     */
    public String getSkillRequirement() {
        return skillRequirement;
    }

    /**
     * @return expected weekly working hours
     */
    public String getHoursPerWeek() {
        return hoursPerWeek;
    }

    /**
     * @return compensation description
     */
    public String getCompensation() {
        return compensation;
    }

    /**
     * @return current job status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Normalises nullable CSV field values.
     *
     * @param value raw CSV field value
     * @return trimmed field value, or an empty string when null
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
