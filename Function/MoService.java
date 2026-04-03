import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MO业务服务类 - 实现MO发布岗位、录用申请者核心功能
 */
public class MoService {
    // 岗位文件存储路径
    private static final String JOB_FILE_PATH = "./data/job.json";
    // 申请文件存储路径
    private static final String APP_FILE_PATH = "./data/application.json";

    /**
     * MO发布岗位功能
     * @param moId MO的唯一ID
     * @param jobName 岗位名称
     * @param jobRequirements 岗位要求
     * @return 发布成功返回岗位对象，失败返回null
     */
    public Job publishJob(String moId, String jobName, String jobRequirements) {
        // 1. 参数校验
        if (moId == null || moId.isBlank() || jobName == null || jobName.isBlank()
                || jobRequirements == null || jobRequirements.isBlank()) {
            System.err.println("发布岗位失败：参数不能为空");
            return null;
        }

        // 2. 生成唯一岗位ID（UUID）
        String jobId = UUID.randomUUID().toString().replace("-", "");
        // 3. 构建岗位对象，默认状态为OPEN
        Job newJob = new Job(jobId, moId, jobName, jobRequirements, "OPEN");

        // 4. 读取现有岗位列表
        List<Job> jobList = JsonFileUtil.readListFromJson(JOB_FILE_PATH, new TypeToken<List<Job>>() {});
        // 5. 添加新岗位（转为可修改的ArrayList）
        List<Job> newJobList = new ArrayList<>(jobList);
        newJobList.add(newJob);

        // 6. 写入JSON文件
        JsonFileUtil.writeListToJson(JOB_FILE_PATH, newJobList);
        System.out.println("岗位发布成功：" + newJob);
        return newJob;
    }

    /**
     * MO录用申请者功能（更新申请状态为ACCEPTED）
     * @param moId 操作的MOID（校验：仅岗位发布者可操作）
     * @param appId 申请ID
     * @return 录用成功返回更新后的申请对象，失败返回null
     */
    public Application acceptApplicant(String moId, String appId) {
        // 1. 参数校验
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) {
            System.err.println("录用申请者失败：参数不能为空");
            return null;
        }

        // 2. 读取申请列表和岗位列表
        List<Application> appList = JsonFileUtil.readListFromJson(APP_FILE_PATH, new TypeToken<List<Application>>() {});
        List<Job> jobList = JsonFileUtil.readListFromJson(JOB_FILE_PATH, new TypeToken<List<Job>>() {});

        // 3. 查找目标申请
        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) {
            System.err.println("录用申请者失败：未找到申请ID为" + appId + "的申请");
            return null;
        }

        // 4. 校验MO权限：仅岗位发布者可录用该岗位的申请者
        String jobId = targetApp.getJobId();
        Job targetJob = null;
        for (Job job : jobList) {
            if (jobId.equals(job.getJobId())) {
                targetJob = job;
                break;
            }
        }
        if (targetJob == null) {
            System.err.println("录用申请者失败：未找到关联的岗位ID为" + jobId + "的岗位");
            return null;
        }
        if (!moId.equals(targetJob.getMoId())) {
            System.err.println("录用申请者失败：MO" + moId + "无该岗位的操作权限");
            return null;
        }

        // 5. 校验申请状态：仅待审核（PENDING）可被录用
        if (!"PENDING".equals(targetApp.getAppStatus())) {
            System.err.println("录用申请者失败：申请状态为" + targetApp.getAppStatus() + "，仅待审核可操作");
            return null;
        }

        // 6. 更新申请状态为ACCEPTED
        List<Application> newAppList = new ArrayList<>(appList);
        for (Application app : newAppList) {
            if (appId.equals(app.getAppId())) {
                app.setAppStatus("ACCEPTED");
                targetApp = app;
                break;
            }
        }

        // 7. 写入JSON文件
        JsonFileUtil.writeListToJson(APP_FILE_PATH, newAppList);
        System.out.println("申请者录用成功：" + targetApp);
        return targetApp;
    }
}