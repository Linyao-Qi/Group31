package Admin;

/**
 * Represents one TA job application row loaded from application.csv.
 *
 * @author Yutong Yao
 * @version 3.0
 */
public class AdminApplicationRecord {
    private final String appId;
    private final String name;
    private final String jobId;
    private final String moId;
    private final String taId;
    private final String major;
    private final String intro;
    private final String skills;
    private final String email;
    private final String cvPath;
    private String appStatus;

    /**
     * Creates an immutable application record except for the mutable application status.
     *
     * @param appId unique application identifier
     * @param name applicant name
     * @param jobId applied job identifier
     * @param moId module organizer identifier
     * @param taId teaching assistant identifier
     * @param major applicant major
     * @param intro applicant introduction
     * @param skills applicant skills
     * @param email applicant email address
     * @param cvPath path to the submitted CV
     * @param appStatus current application status
     */
    public AdminApplicationRecord(
            String appId,
            String name,
            String jobId,
            String moId,
            String taId,
            String major,
            String intro,
            String skills,
            String email,
            String cvPath,
            String appStatus
    ) {
        this.appId = appId;
        this.name = name;
        this.jobId = jobId;
        this.moId = moId;
        this.taId = taId;
        this.major = major;
        this.intro = intro;
        this.skills = skills;
        this.email = email;
        this.cvPath = cvPath;
        this.appStatus = appStatus;
    }

    public String getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public String getJobId() {
        return jobId;
    }

    public String getMoId() {
        return moId;
    }

    public String getTaId() {
        return taId;
    }

    public String getMajor() {
        return major;
    }

    public String getIntro() {
        return intro;
    }

    public String getSkills() {
        return skills;
    }

    public String getEmail() {
        return email;
    }

    public String getCvPath() {
        return cvPath;
    }

    public String getAppStatus() {
        return appStatus;
    }

    /**
     * Updates the application status used by admin workflows.
     *
     * @param appStatus new application status
     */
    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }
}
