package com;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoApplicantReviewService {

    // ✅ 完全仿照AuthUtil/MoService，路径动态初始化
    private static String JOB_FILE_PATH = "data/job.csv";
    private static String TA_CSV_PATH = "data/ta_profiles.csv";
    private static Map<String, String> TA_SKILL_MAP;
    private static List<String> ALL_TA_IDS;

    // ✅ 新增init方法，ServletContext获取真实路径（核心修复）
    public static void init(ServletContext context) {
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
        TA_CSV_PATH = context.getRealPath("data/ta_profiles.csv");
        
        // 路径初始化后再加载数据，避免空指针
        TA_SKILL_MAP = TaCsvUtil.loadTaSkills(TA_CSV_PATH);
        ALL_TA_IDS = TaCsvUtil.getAllTaIds(TA_CSV_PATH);
        
        // 调试日志，确认路径正确
        System.out.println("===== MoApplicantReviewService 初始化 =====");
        System.out.println("JOB_FILE_PATH: " + JOB_FILE_PATH);
        System.out.println("TA_CSV_PATH: " + TA_CSV_PATH);
        System.out.println("TA数量: " + (TA_SKILL_MAP != null ? TA_SKILL_MAP.size() : 0));
    }

    // ====================== 下面是你原来的业务代码，完全不动 ======================
    public boolean isValidMO(String moId, String password) {
        return AuthUtil.authenticateMO(moId, password);
    }

    public List<Application> getApplications(String moId) {
        List<Application> realApps = new ArrayList<>();
        System.out.println("===== 调试日志 - getApplications方法 =====");
        System.out.println("当前传入的MO ID：" + moId);

        List<Job> moJobs = getMoPublishedJobs(moId);
        System.out.println("该MO发布的岗位数量：" + moJobs.size());
        System.out.println("当前系统中所有TA数量：" + (ALL_TA_IDS == null ? 0 : ALL_TA_IDS.size()));

        if (moJobs.isEmpty() || ALL_TA_IDS == null || ALL_TA_IDS.isEmpty()) {
            System.err.println("【异常】无岗位或无TA，无法生成申请！");
            return realApps;
        }

        for (Job job : moJobs) {
            for (String taId : ALL_TA_IDS) {
                Application app = new Application();
                app.setAppId("APP_" + UUID.randomUUID().toString().substring(0, 8));
                app.setJobId(job.getJobId());
                app.setTaId(taId);
                app.setAppStatus("PENDING");
                realApps.add(app);
            }
        }
        return realApps;
    }

    public String getTaSkill(String taId) {
        if (TA_SKILL_MAP == null) return "";
        return TA_SKILL_MAP.getOrDefault(taId, "");
    }

    public String getSkillRequirement(String jobId) {
        List<Job> jobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        for (Job job : jobs) {
            if (jobId.equals(job.getJobId())) {
                return job.getSkillRequirement();
            }
        }
        return "";
    }

    private List<Job> getMoPublishedJobs(String moId) {
        List<Job> allJobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        List<Job> moJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (moId.equals(job.getMoId())) {
                moJobs.add(job);
            }
        }
        return moJobs;
    }
}