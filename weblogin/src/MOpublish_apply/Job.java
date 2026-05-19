package com;

import java.io.Serializable;

/**
 * Job Entity Class
 * <p>Encapsulates all data fields for a teaching assistant position.
 * Maps directly to job.csv and is used across the system for position management,
 * application matching, and recruitment workflow.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-04-13
 */
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Unique job ID */
    private String jobId;

    /** Module Organizer (MO) ID who created the job */
    private String moId;

    /** Course or subject name */
    private String subject;

    /** Type of teaching work (tutorial, lab, grading, etc.) */
    private String workType;

    /** Detailed job description */
    private String description;

    /** Required skills for the position */
    private String skillRequirement;

    /** Weekly working hours */
    private int hoursPerWeek;

    /** Salary or compensation information */
    private String compensation;

    /** Job status (OPEN/CLOSED) */
    private String status;

    /** Maximum number of TAs that can be hired */
    private int maxHire;

    /**
     * Default no-argument constructor
     */
    public Job() {}

    /**
     * Full-field constructor for Job entity
     * @param jobId unique job ID
     * @param moId ID of the creator MO
     * @param subject course/subject name
     * @param workType type of work
     * @param description detailed job description
     * @param skillRequirement required skills
     * @param hoursPerWeek weekly working hours
     * @param compensation payment details
     * @param status job open/closed status
     * @param maxHire maximum recruitment quota
     */
    public Job(String jobId, String moId, String subject, String workType, String description,
               String skillRequirement, int hoursPerWeek, String compensation, String status, int maxHire) {
        this.jobId = jobId;
        this.moId = moId;
        this.subject = subject;
        this.workType = workType;
        this.description = description;
        this.skillRequirement = skillRequirement;
        this.hoursPerWeek = hoursPerWeek;
        this.compensation = compensation;
        this.status = status;
        this.maxHire = maxHire;
    }

    /**
     * Get job ID
     * @return unique job identifier
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
     * @param moId MO user ID
     */
    public void setMoId(String moId) { this.moId = moId; }

    /**
     * Get subject name
     * @return course/subject name
     */
    public String getSubject() { return subject; }

    /**
     * Set subject name
     * @param subject course/subject name
     */
    public void setSubject(String subject) { this.subject = subject; }

    /**
     * Get work type
     * @return type of teaching work
     */
    public String getWorkType() { return workType; }

    /**
     * Set work type
     * @param workType teaching work type
     */
    public void setWorkType(String workType) { this.workType = workType; }

    /**
     * Get job description
     * @return detailed job description text
     */
    public String getDescription() { return description; }

    /**
     * Set job description
     * @param description detailed job description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Get required skills
     * @return skill requirements for the job
     */
    public String getSkillRequirement() { return skillRequirement; }

    /**
     * Set required skills
     * @param skillRequirement job skill requirements
     */
    public void setSkillRequirement(String skillRequirement) { this.skillRequirement = skillRequirement; }

    /**
     * Get weekly working hours
     * @return hours per week
     */
    public int getHoursPerWeek() { return hoursPerWeek; }

    /**
     * Set weekly working hours
     * @param hoursPerWeek weekly working hours
     */
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    /**
     * Get compensation information
     * @return salary/payment details
     */
    public String getCompensation() { return compensation; }

    /**
     * Set compensation
     * @param compensation salary or payment information
     */
    public void setCompensation(String compensation) { this.compensation = compensation; }

    /**
     * Get job status
     * @return OPEN or CLOSED status
     */
    public String getStatus() { return status; }

    /**
     * Set job status
     * @param status job open/closed status
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Get maximum hiring quota
     * @return maximum number of TAs allowed
     */
    public int getMaxHire() { return maxHire; }

    /**
     * Set maximum hiring quota
     * @param maxHire maximum allowed hires
     */
    public void setMaxHire(int maxHire) { this.maxHire = maxHire; }
}
