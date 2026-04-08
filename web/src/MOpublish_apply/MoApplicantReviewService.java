package com;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MO申请者审核服务层：读取真实TA数据 + 所有TA申请所有岗位 + TA数量动态适配
 */
public class MoApplicantReviewService {
	private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String JOB_FILE_PATH = PROJECT_PATH + "/data/job.csv";
    // 真实TA数据文件路径（与job.csv同目录）
    private static final String TA_CSV_PATH = PROJECT_PATH + "/data/ta_profiles.csv";

    // 动态加载：csv增删TA，这里自动同步，无硬编码
    private static final Map<String, String> TA_SKILL_MAP = TaCsvUtil.loadTaSkills(TA_CSV_PATH);
    private static final List<String> ALL_TA_IDS = TaCsvUtil.getAllTaIds(TA_CSV_PATH);

    // ====================== 调试代码 START ======================
    // 静态代码块：初始化时打印核心数据加载情况，定位基础数据读取问题
    static {
        System.out.println("===== 调试日志 - 初始化阶段 =====");
        // 打印文件路径（排查路径错误）
        System.out.println("JOB_FILE_PATH 路径：" + JOB_FILE_PATH);
        System.out.println("TA_CSV_PATH 路径：" + TA_CSV_PATH);
        
        // 打印TA数据加载情况
        System.out.println("TA_SKILL_MAP 加载的TA数量：" + (TA_SKILL_MAP == null ? "NULL" : TA_SKILL_MAP.size()));
        System.out.println("ALL_TA_IDS 加载的TA ID列表：" + (ALL_TA_IDS == null ? "NULL" : ALL_TA_IDS));
        if (TA_SKILL_MAP == null || TA_SKILL_MAP.isEmpty()) {
            System.err.println("【异常】TA技能数据未读取到！可能是ta_profiles.csv文件不存在/路径错误/内容为空");
        }
        if (ALL_TA_IDS == null || ALL_TA_IDS.isEmpty()) {
            System.err.println("【异常】TA ID列表未读取到！可能是ta_profiles.csv文件不存在/路径错误/内容为空");
        }
        System.out.println("===== 调试日志 - 初始化阶段结束 =====\n");
    }
    // ====================== 调试代码 END ======================

    /**
     * 校验MO账号密码（复用原有逻辑，无改动）
     */
    public boolean isValidMO(String moId, String password) {
        return AuthUtil.authenticateMO(moId, password);
    }

    /**
     * 核心逻辑：所有TA申请所有岗位 + TA数量动态适配
     * csv中新增/删除TA，无需修改代码，自动生成对应申请
     */
    public List<Application> getApplications(String moId) {
        List<Application> realApps = new ArrayList<>();
        
        // ====================== 调试代码 START ======================
        System.out.println("===== 调试日志 - getApplications方法 =====");
        System.out.println("当前传入的MO ID：" + moId);
        // ====================== 调试代码 END ======================

        // 1. 获取当前MO发布的所有真实岗位
        List<Job> moJobs = getMoPublishedJobs(moId);

        // ====================== 调试代码 START ======================
        System.out.println("该MO发布的岗位数量：" + moJobs.size());
        if (moJobs.isEmpty()) {
            System.err.println("【异常】该MO未发布任何岗位！MO ID：" + moId);
        } else {
            System.out.println("该MO发布的岗位列表：");
            for (Job job : moJobs) {
                System.out.println("  - 岗位ID：" + job.getJobId() + "，MO ID：" + job.getMoId() + "，技能要求：" + job.getSkillRequirement());
            }
        }
        System.out.println("当前系统中所有TA数量：" + (ALL_TA_IDS == null ? 0 : ALL_TA_IDS.size()));
        // ====================== 调试代码 END ======================

        // 边界判断：无岗位 或 无TA，直接返回空列表
        if (moJobs.isEmpty() || ALL_TA_IDS.isEmpty()) {
            // ====================== 调试代码 START ======================
            System.err.println("【异常】无岗位或无TA，无法生成申请！");
            System.err.println("  - 无岗位：" + moJobs.isEmpty());
            System.err.println("  - 无TA：" + (ALL_TA_IDS == null || ALL_TA_IDS.isEmpty()));
            System.out.println("===== 调试日志 - getApplications方法结束 =====\n");
            // ====================== 调试代码 END ======================
            return realApps;
        }

        // 2. 遍历MO的每个岗位 → 遍历所有TA → 为每个TA生成1条申请（所有TA申请所有岗位）
        int generateCount = 0; // 调试用：统计生成的申请数
        for (Job job : moJobs) {
            for (String taId : ALL_TA_IDS) {
                Application app = new Application();
                app.setAppId("APP_" + UUID.randomUUID().toString().substring(0, 8)); // 唯一申请ID
                app.setJobId(job.getJobId()); // 关联当前岗位
                app.setTaId(taId); // 绑定真实TA ID（动态读取，无硬编码）
                app.setAppStatus("PENDING"); // 申请状态默认待审核
                realApps.add(app);
                
                // ====================== 调试代码 START ======================
                generateCount++;
                System.out.println("生成申请：申请ID=" + app.getAppId() + "，岗位ID=" + job.getJobId() + "，TA ID=" + taId);
                // ====================== 调试代码 END ======================
            }
        }

        // ====================== 调试代码 START ======================
        System.out.println("最终生成的申请总数：" + generateCount);
        if (generateCount == 0) {
            System.err.println("【异常】遍历完成但未生成任何申请！");
        }
        System.out.println("===== 调试日志 - getApplications方法结束 =====\n");
        // ====================== 调试代码 END ======================

        return realApps;
    }

    /**
     * 读取真实TA的技能（从csv动态获取，无虚拟数据）
     */
    public String getTaSkill(String taId) {
        // ====================== 调试代码 START ======================
        System.out.println("===== 调试日志 - getTaSkill方法 =====");
        System.out.println("查询TA技能的TA ID：" + taId);
        System.out.println("TA_SKILL_MAP中是否包含该TA：" + TA_SKILL_MAP.containsKey(taId));
        // ====================== 调试代码 END ======================
        
        // 无匹配TA则返回空字符串，避免空指针
        String skill = TA_SKILL_MAP.getOrDefault(taId, "");
        
        // ====================== 调试代码 START ======================
        if (skill.isEmpty()) {
            System.err.println("【异常】该TA无技能数据！TA ID：" + taId);
        } else {
            System.out.println("该TA的技能：" + skill);
        }
        System.out.println("===== 调试日志 - getTaSkill方法结束 =====\n");
        // ====================== 调试代码 END ======================
        
        return skill;
    }
    
    /**
     * 根据jobId获取岗位技能要求（原有逻辑，无改动）
     */
    public String getSkillRequirement(String jobId) {
        // ====================== 调试代码 START ======================
        System.out.println("===== 调试日志 - getSkillRequirement方法 =====");
        System.out.println("查询技能要求的岗位ID：" + jobId);
        // ====================== 调试代码 END ======================
        
        List<Job> jobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        
        // ====================== 调试代码 START ======================
        System.out.println("从job.csv读取的所有岗位数量：" + jobs.size());
        // ====================== 调试代码 END ======================
        
        for (Job job : jobs) {
            if (jobId.equals(job.getJobId())) {
                // ====================== 调试代码 START ======================
                System.out.println("找到岗位，技能要求：" + job.getSkillRequirement());
                System.out.println("===== 调试日志 - getSkillRequirement方法结束 =====\n");
                // ====================== 调试代码 END ======================
                return job.getSkillRequirement();
            }
        }
        
        // ====================== 调试代码 START ======================
        System.err.println("【异常】未找到该岗位的技能要求！岗位ID：" + jobId);
        System.out.println("===== 调试日志 - getSkillRequirement方法结束 =====\n");
        // ====================== 调试代码 END ======================
        
        return "";
    }

    /**
     * 获取MO发布的所有岗位（原有逻辑，无改动）
     */
    private List<Job> getMoPublishedJobs(String moId) {
        // ====================== 调试代码 START ======================
        System.out.println("===== 调试日志 - getMoPublishedJobs方法 =====");
        System.out.println("查询MO发布岗位的MO ID：" + moId);
        // ====================== 调试代码 END ======================
        
        List<Job> allJobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        
        // ====================== 调试代码 START ======================
        System.out.println("从job.csv读取的所有岗位数量：" + allJobs.size());
        if (allJobs.isEmpty()) {
            System.err.println("【异常】job.csv中未读取到任何岗位数据！");
        } else {
            System.out.println("所有岗位列表：");
            for (Job job : allJobs) {
                System.out.println("  - 岗位ID：" + job.getJobId() + "，所属MO ID：" + job.getMoId());
            }
        }
        // ====================== 调试代码 END ======================
        
        List<Job> moJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (moId.equals(job.getMoId())) {
                moJobs.add(job);
            }
        }

        // ====================== 调试代码 START ======================
        System.out.println("匹配到该MO的岗位数量：" + moJobs.size());
        System.out.println("===== 调试日志 - getMoPublishedJobs方法结束 =====\n");
        // ====================== 调试代码 END ======================
        
        return moJobs;
    }
}