import java.io.Serializable;

/**
 * 岗位实体类 - 对应MO发布的TA岗位
 */
public class Job implements Serializable {
    private static final long serialVersionUID = 1L;
    // 岗位唯一ID
    private String jobId;
    // 发布者（MO）ID
    private String moId;
    // 岗位名称（如：课程TA、监考助理）
    private String jobName;
    // 岗位要求
    private String jobRequirements;
    // 岗位状态：OPEN(开放)、CLOSED(关闭)
    private String jobStatus;

    // 无参构造（JSON反序列化必需）
    public Job() {}

    // 全参构造
    public Job(String jobId, String moId, String jobName, String jobRequirements, String jobStatus) {
        this.jobId = jobId;
        this.moId = moId;
        this.jobName = jobName;
        this.jobRequirements = jobRequirements;
        this.jobStatus = jobStatus;
    }

    // Getter & Setter
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

    // 重写toString
    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", moId='" + moId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobRequirements='" + jobRequirements + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                '}';
    }
}