package com;

public class Application {
    
    // 对应CSV：appId,name,jobId,moId,taId,major,intro,skills,email,CVpath,appStatus
    private String appId;
    private String name;
    private String jobId;
    private String moId;
    private String taId;
    private String major;
    private String intro;
    private String skills;
    private String email;
    private String CVpath;
    private String appStatus;

    public Application() {}

    // 全参构造
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

    // Getter & Setter
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCVpath() { return CVpath; }
    public void setCVpath(String CVpath) { this.CVpath = CVpath; }

    public String getAppStatus() { return appStatus; }
    public void setAppStatus(String appStatus) { this.appStatus = appStatus; }
}