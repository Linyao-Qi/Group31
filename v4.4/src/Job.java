package com;
import java.io.Serializable;

public class Job implements Serializable {
    private static final long serialVersionUID = 1L;
    private String jobId;
    private String moId;
    private String jobName;
    private String jobRequirements;
    private String jobStatus;

    public Job() {}

    public Job(String jobId, String moId, String jobName, String jobRequirements, String jobStatus) {
        this.jobId = jobId;
        this.moId = moId;
        this.jobName = jobName;
        this.jobRequirements = jobRequirements;
        this.jobStatus = jobStatus;
    }

    // 全量Getter&Setter
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public String getJobRequirements() { return jobRequirements; }
    public void setJobRequirements(String jobRequirements) { this.jobRequirements = jobRequirements; }
    public String getJobStatus() { return jobStatus; }
    public void setJobStatus(String jobStatus) { this.jobStatus = jobStatus; }
}