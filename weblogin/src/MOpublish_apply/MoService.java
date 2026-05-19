package com;
import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class MoService {
    public static String JOB_FILE_PATH;
    public static String APP_FILE_PATH;

    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        APP_FILE_PATH = context.getRealPath("data/application.csv");
    }

    // 获取所有岗位（修复空指针）
    public List<Job> getAllJobs() {
        if (JOB_FILE_PATH == null || JOB_FILE_PATH.isEmpty()) {
            return new ArrayList<>();
        }
        return CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
    }

    // 发布岗位（新增 maxHire 参数）
    public Job publishJob(String moId, String password, String subject, String workType,
                          String description, String skillRequirement, int hoursPerWeek, 
                          String compensation, int maxHire) { // 新增maxHire参数
        if (JOB_FILE_PATH == null) return null;
        if (!AuthUtil.authenticateMO(moId, password)) return null;
        return publishJobForMo(moId, subject, workType, description, skillRequirement,
                hoursPerWeek, compensation, maxHire);
    }

    public Job publishJobForMo(String moId, String subject, String workType,
                               String description, String skillRequirement, int hoursPerWeek,
                               String compensation, int maxHire) {
        if (JOB_FILE_PATH == null) return null;
        if (moId == null || moId.isBlank() || subject == null || subject.isBlank()
                || workType == null || workType.isBlank() || description == null || description.isBlank()
                || skillRequirement == null || skillRequirement.isBlank() || compensation == null || compensation.isBlank()
                || maxHire <= 0) { // 校验最大录用人数为正数
            return null;
        }

        String randomCode = generateRandomCode(6);
        String jobId = moId + randomCode;

        // 使用包含maxHire的构造器创建岗位
        Job newJob = new Job(
                jobId, moId, subject, workType, description,
                skillRequirement, hoursPerWeek, compensation, "OPEN", maxHire
        );

        List<Job> jobList = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        jobList.add(newJob);
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);
        return newJob;
    }

    // 兼容旧版发布岗位接口（默认maxHire=1）
    public Job publishJob(String moId, String password, String subject, String workType,
                          String description, String skillRequirement, int hoursPerWeek, String compensation) {
        return publishJob(moId, password, subject, workType, description, skillRequirement,
                hoursPerWeek, compensation, 1);
    }

    // ==============================================
    // 录用申请者（增加最大录用人数校验）
    // ==============================================
    public Application acceptApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) return null;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = getAllJobs();

        // 1. 找到目标申请
        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return null;
        if (!"PENDING".equals(targetApp.getAppStatus())) return null;

        // 2. 找到对应岗位
        Job targetJob = null;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                break;
            }
        }
        if (targetJob == null) return null;

        // 3. 统计该岗位已录用的人数
        int acceptedCount = 0;
        for (Application app : appList) {
            if (targetApp.getJobId().equals(app.getJobId()) 
                    && "ACCEPTED".equals(app.getAppStatus())) {
                acceptedCount++;
            }
        }

        // 4. 校验是否超过最大录用人数
        if (acceptedCount >= targetJob.getMaxHire()) {
            // 超过限制，返回null（前端据此提示）
            return null;
        }

        // 5. 正常录用流程
        targetApp.setAppStatus("ACCEPTED");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        // 6. 更新岗位状态：仅当录用后达到最大人数时才设为FILLED
        if (acceptedCount + 1 >= targetJob.getMaxHire()) {
            targetJob.setStatus("FILLED");
        }
        CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);

        return targetApp;
    }

    // 取消录用（同步调整岗位状态逻辑）
    public boolean cancelApplicant(String moId, String cancelAppId) {
        if (moId == null || moId.isBlank() || cancelAppId == null || cancelAppId.isBlank()) return false;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Job> jobList = getAllJobs();

        Application targetApp = null;
        for (Application app : appList) {
            if (cancelAppId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return false;
        if (!"ACCEPTED".equals(targetApp.getAppStatus())) return false;

        // 取消录用，恢复为待处理
        targetApp.setAppStatus("PENDING");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        // 重新计算该岗位已录用人数，判断是否恢复为OPEN
        Job targetJob = null;
        int acceptedCount = 0;
        for (Job job : jobList) {
            if (targetApp.getJobId().equals(job.getJobId())) {
                targetJob = job;
                // 统计当前已录用人数
                for (Application app : appList) {
                    if (job.getJobId().equals(app.getJobId()) 
                            && "ACCEPTED".equals(app.getAppStatus())) {
                        acceptedCount++;
                    }
                }
                break;
            }
        }

        // 若取消后未达最大录用人数，恢复岗位为OPEN
        if (targetJob != null && acceptedCount < targetJob.getMaxHire()) {
            targetJob.setStatus("OPEN");
            CsvFileUtil.writeJobListToCsv(JOB_FILE_PATH, jobList);
        }

        return true;
    }

    // ==============================================
    // 新增：拒绝申请者
    // ==============================================
    public Application rejectApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) return null;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);

        // 1. 找到目标申请
        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return null;
        // 仅允许拒绝「待处理(PENDING)」状态的申请
        if (!"PENDING".equals(targetApp.getAppStatus())) return null;

        // 2. 执行拒绝：状态改为REJECTED
        targetApp.setAppStatus("REJECTED");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        // 拒绝不影响岗位最大录用人数，无需修改岗位状态
        return targetApp;
    }

    // ==============================================
    // 新增：取消拒绝（恢复为PENDING）
    // ==============================================
    public Application cancelRejectApplicant(String moId, String appId) {
        if (moId == null || moId.isBlank() || appId == null || appId.isBlank()) return null;

        List<Application> appList = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);

        // 1. 找到目标申请
        Application targetApp = null;
        for (Application app : appList) {
            if (appId.equals(app.getAppId()) && moId.equals(app.getMoId())) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) return null;
        // 仅允许取消「已拒绝(REJECTED)」状态的申请
        if (!"REJECTED".equals(targetApp.getAppStatus())) return null;

        // 2. 取消拒绝：状态恢复为PENDING
        targetApp.setAppStatus("PENDING");
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, appList);

        return targetApp;
    }

    // 获取当前 MO 的所有申请（完整信息）
    public List<Application> getAllApps(String moId, String password, boolean isAdmin) {
        if (!AuthUtil.authenticateMO(moId, password)) return null;

        return getAllAppsForMo(moId);
    }

    public List<Application> getAllAppsForMo(String moId) {
        if (moId == null || moId.isBlank()) return new ArrayList<>();

        List<Application> allApps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        List<Application> result = new ArrayList<>();
        for (Application app : allApps) {
            if (moId.equals(app.getMoId())) {
                result.add(app);
            }
        }
        return result;
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
