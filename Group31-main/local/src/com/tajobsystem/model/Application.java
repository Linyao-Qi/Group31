package com.tajobsystem.model;

import java.io.Serializable;

public class Application implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appId;
    private String jobId;
    private String taId;
    private String appStatus;
    private String cvFilePath;   // path to the CV PDF submitted with this application

    public Application() {}

    public Application(String appId, String jobId, String taId, String appStatus) {
        this.appId = appId;
        this.jobId = jobId;
        this.taId = taId;
        this.appStatus = appStatus;
    }

    public Application(String appId, String jobId, String taId, String appStatus, String cvFilePath) {
        this(appId, jobId, taId, appStatus);
        this.cvFilePath = cvFilePath;
    }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    public String getAppStatus() { return appStatus; }
    public void setAppStatus(String appStatus) { this.appStatus = appStatus; }
    public String getCvFilePath() { return cvFilePath; }
    public void setCvFilePath(String cvFilePath) { this.cvFilePath = cvFilePath; }

    @Override
    public String toString() {
        return "Application{appId='" + appId + "', jobId='" + jobId + "', taId='" + taId + "', status=" + appStatus + "}";
    }
}
