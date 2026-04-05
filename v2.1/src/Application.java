package com;
import java.io.Serializable;

public class Application implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appId;
    private String jobId;
    private String taId;
    private String appStatus;

    public Application() {}

    public Application(String appId, String jobId, String taId, String appStatus) {
        this.appId = appId;
        this.jobId = jobId;
        this.taId = taId;
        this.appStatus = appStatus;
    }

    // 全量Getter&Setter
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    public String getAppStatus() { return appStatus; }
    public void setAppStatus(String appStatus) { this.appStatus = appStatus; }
}