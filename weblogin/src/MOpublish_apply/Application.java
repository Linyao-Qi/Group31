package com;

/**
 * Application Entity Class
 * <p>Encapsulates all information for a teaching assistant job application.
 * Maps directly to the fields in application.csv and is used for data storage,
 * data transfer, and business logic processing across the system.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class Application {

    // CSV fields: appId,name,jobId,moId,taId,major,intro,skills,email,CVpath,appStatus

    /** Unique application ID */
    private String appId;

    /** Applicant full name */
    private String name;

    /** Related job position ID */
    private String jobId;

    /** Module Organizer (MO) user ID */
    private String moId;

    /** Teaching Assistant (TA) user ID */
    private String taId;

    /** Applicant major */
    private String major;

    /** Applicant self-introduction */
    private String intro;

    /** Applicant skills */
    private String skills;

    /** Applicant contact email */
    private String email;

    /** File path of the uploaded CV */
    private String CVpath;

    /** Current application status */
    private String appStatus;

    /**
     * Default no-argument constructor
     */
    public Application() {}

    /**
     * Full-field constructor
     * @param appId unique application ID
     * @param name applicant name
     * @param jobId job position ID
     * @param moId module organizer ID
     * @param taId teaching assistant ID
     * @param major applicant major
     * @param intro applicant self-introduction
     * @param skills applicant skills
     * @param email contact email
     * @param CVpath CV file storage path
     * @param appStatus current application status
     */
    public Application(String appId, String name, String jobId, String moId, String taId,
                       String major, String intro, String skills, String email,
                       String CVpath, String appStatus) {
        this.appId = appId;
        this.name = name;
        this.jobId = jobId;
        this.moId = moId;
        this.taId = taId;
        this.major = major;
        this.intro = intro;
        this.skills = skills;
        this.email = email;
        this.CVpath = CVpath;
        this.appStatus = appStatus;
    }

    /**
     * Get application ID
     * @return unique ID of the application
     */
    public String getAppId() { return appId; }

    /**
     * Set application ID
     * @param appId unique ID to set
     */
    public void setAppId(String appId) { this.appId = appId; }

    /**
     * Get applicant name
     * @return full name of applicant
     */
    public String getName() { return name; }

    /**
     * Set applicant name
     * @param name full name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Get job ID
     * @return ID of the applied job
     */
    public String getJobId() { return jobId; }

    /**
     * Set job ID
     * @param jobId job ID to set
     */
    public void setJobId(String jobId) { this.jobId = jobId; }

    /**
     * Get MO ID
     * @return ID of the module organizer
     */
    public String getMoId() { return moId; }

    /**
     * Set MO ID
     * @param moId module organizer ID to set
     */
    public void setMoId(String moId) { this.moId = moId; }

    /**
     * Get TA ID
     * @return ID of the teaching assistant applicant
     */
    public String getTaId() { return taId; }

    /**
     * Set TA ID
     * @param taId teaching assistant ID to set
     */
    public void setTaId(String taId) { this.taId = taId; }

    /**
     * Get applicant major
     * @return major of the applicant
     */
    public String getMajor() { return major; }

    /**
     * Set applicant major
     * @param major major to set
     */
    public void setMajor(String major) { this.major = major; }

    /**
     * Get self-introduction
     * @return applicant's self-introduction text
     */
    public String getIntro() { return intro; }

    /**
     * Set self-introduction
     * @param intro self-introduction text to set
     */
    public void setIntro(String intro) { this.intro = intro; }

    /**
     * Get applicant skills
     * @return skill description string
     */
    public String getSkills() { return skills; }

    /**
     * Set applicant skills
     * @param skills skill description string to set
     */
    public void setSkills(String skills) { this.skills = skills; }

    /**
     * Get contact email
     * @return applicant's email address
     */
    public String getEmail() { return email; }

    /**
     * Set contact email
     * @param email email address to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Get CV file path
     * @return storage path of the uploaded CV
     */
    public String getCVpath() { return CVpath; }

    /**
     * Set CV file path
     * @param CVpath storage path to set
     */
    public void setCVpath(String CVpath) { this.CVpath = CVpath; }

    /**
     * Get application status
     * @return current status such as PENDING, HIRED, REJECTED
     */
    public String getAppStatus() { return appStatus; }

    /**
     * Set application status
     * @param appStatus status value to set
     */
    public void setAppStatus(String appStatus) { this.appStatus = appStatus; }
}
