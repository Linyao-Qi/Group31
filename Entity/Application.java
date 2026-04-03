import java.io.Serializable;

/**
 * 申请实体类 - 对应TA的岗位申请，MO基于此进行录用操作
 */
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;
    // 申请唯一ID
    private String appId;
    // 关联岗位ID
    private String jobId;
    // 申请者TAID
    private String taId;
    // 申请状态：PENDING(待审核)、ACCEPTED(录用)、REJECTED(拒绝)
    private String appStatus;

    // 无参构造
    public Application() {}

    // 全参构造
    public Application(String appId, String jobId, String taId, String appStatus) {
        this.appId = appId;
        this.jobId = jobId;
        this.taId = taId;
        this.appStatus = appStatus;
    }

    // Getter & Setter
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    public String getAppStatus() { return appStatus; }
    public void setAppStatus(String appStatus) { this.appStatus = appStatus; }

    // 重写toString
    @Override
    public String toString() {
        return "Application{" +
                "appId='" + appId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", taId='" + taId + '\'' +
                ", appStatus='" + appStatus + '\'' +
                '}';
    }
}