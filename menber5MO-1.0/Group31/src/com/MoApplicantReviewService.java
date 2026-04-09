package com;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MO申请者审核服务层：生成虚拟申请数据 + 权限校验
 */
public class MoApplicantReviewService {
	private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String JOB_FILE_PATH = PROJECT_PATH + "/data/job.csv";
    
    // 虚拟TA技能库（模拟真实TA的技能数据）
    private static final String[] TA_SKILLS_POOL = {
        "Java,Spring,MySQL",
        "Python,Django,Redis",
        "Java,MySQL,Redis",
        "Python,Flask,MongoDB",
        "Java,SpringBoot,MySQL"
    };

    /**
     * 校验MO账号密码（复用AuthUtil）
     */
    public boolean isValidMO(String moId, String password) {
        return AuthUtil.authenticateMO(moId, password);
    }

    /**
     * 获取当前MO发布岗位的申请列表（虚拟数据）
     * @param moId MO账号
     * @return 虚拟申请列表
     */
    public List<Application> getApplications(String moId) {
        List<Application> virtualApps = new ArrayList<>();
        // 1. 获取该MO发布的所有岗位
        List<Job> moJobs = getMoPublishedJobs(moId);
        if (moJobs.isEmpty()) {
            return virtualApps; // 无岗位则无申请
        }

        // 2. 为每个岗位生成2-3条虚拟申请
        for (Job job : moJobs) {
            int appCount = 2 + (int) (Math.random() * 2); // 每个岗位2-3条申请
            for (int i = 0; i < appCount; i++) {
                Application app = new Application();
                app.setAppId("APP_" + UUID.randomUUID().toString().substring(0, 8)); // 随机申请ID
                app.setJobId(job.getJobId()); // 关联当前岗位
                app.setTaId("TA_" + (100 + (int) (Math.random() * 10))); // 虚拟TA ID
                app.setAppStatus("PENDING"); // 申请状态默认待审核
                virtualApps.add(app);
            }
        }
        return virtualApps;
    }

    /**
     * 获取MO发布的所有岗位（复用CsvFileUtil读取真实岗位数据）
     */
    private List<Job> getMoPublishedJobs(String moId) {
        List<Job> allJobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH); // 路径
        List<Job> moJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (moId.equals(job.getMoId())) {
                moJobs.add(job);
            }
        }
        return moJobs;
    }

    /**
     * 获取虚拟TA的技能（模拟数据）
     */
    public String getTaSkill(String taId) {
        // 按TA ID哈希取模，保证同一个TA技能固定
        int index = Math.abs(taId.hashCode()) % TA_SKILLS_POOL.length;
        return TA_SKILLS_POOL[index];
    }

    /**
     * 获取岗位要求（复用JobDao）
     */
    public String getJobReq(String jobId) {
        Job job = JobDao.getJobById(jobId);
        return job != null ? job.getJobRequirements() : "";
    }
}