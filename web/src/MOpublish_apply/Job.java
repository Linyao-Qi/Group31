package com;
import java.io.Serializable;

public class Job implements Serializable {
    private static final long serialVersionUID = 1L;

    private String jobId;
    private String moId;
    private String subject;
    private String workType;
    private String description;
    private String skillRequirement;
    private int hoursPerWeek;
    private String compensation;
    private String status;

    public Job() {}

    public Job(String jobId, String moId, String subject, String workType, String description,
               String skillRequirement, int hoursPerWeek, String compensation, String status) {
        this.jobId = jobId;
        this.moId = moId;
        this.subject = subject;
        this.workType = workType;
        this.description = description;
        this.skillRequirement = skillRequirement;
        this.hoursPerWeek = hoursPerWeek;
        this.compensation = compensation;
        this.status = status;
    }

    // Getter + Setter
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSkillRequirement() { return skillRequirement; }
    public void setSkillRequirement(String skillRequirement) { this.skillRequirement = skillRequirement; }

    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public String getCompensation() { return compensation; }
    public void setCompensation(String compensation) { this.compensation = compensation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}