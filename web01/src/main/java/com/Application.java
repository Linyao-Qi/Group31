package com;

public class Application {
    
    private String appId;
    private String jobId;
    private String taId;
    private String intro;
    private String appStatus;

    public Application() {}

    public Application(String appId, String jobId, String taId, String intro, String appStatus) {
        this.appId = appId;
        this.jobId = jobId;
        this.taId = taId;
        this.intro = intro;
        this.appStatus = appStatus;
    }

    public String getAppId() {return appId;}
    public void setAppId(String appId) {this.appId = appId;}
    public String getJobId() {return jobId;}
    public void setJobId(String jobId) {this.jobId = jobId;}
    public String getTaId() {return taId;}
    public void setTaId(String taId) {this.taId = taId;}
    public String getIntro() {return intro;}
    public void setIntro(String intro) {this.intro = intro;}
    public String getAppStatus() {return appStatus;}
    public void setAppStatus(String appStatus) {this.appStatus = appStatus;}
}