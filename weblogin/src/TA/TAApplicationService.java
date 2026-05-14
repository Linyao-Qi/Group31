package TA;

import com.Application;
import com.CsvFileUtil;
import com.Job;
import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

/**
 * TA 申请业务逻辑
 * 移植自 _previous/ApplicationService，适配 web 模块 CSV schema
 */
public class TAApplicationService {

    private static String APP_FILE_PATH;
    private static String JOB_FILE_PATH;

    public static void init(ServletContext context) {
        APP_FILE_PATH = context.getRealPath("data/application.csv");
        JOB_FILE_PATH = context.getRealPath("data/job.csv");
    }

    /**
     * 检查是否已有有效申请（非 WITHDRAWN 状态），防止重复投递
     */
    public static boolean hasActiveApplication(String taId, String jobId) {
        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : apps) {
            if (taId.equals(app.getTaId()) && jobId.equals(app.getJobId())
                    && !"WITHDRAWN".equals(app.getAppStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取已有有效申请的状态（PENDING/ACCEPTED/REJECTED），无则返回 null
     */
    public static String getActiveApplicationStatus(String taId, String jobId) {
        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : apps) {
            if (taId.equals(app.getTaId()) && jobId.equals(app.getJobId())
                    && !"WITHDRAWN".equals(app.getAppStatus())) {
                return app.getAppStatus();
            }
        }
        return null;
    }

    /**
     * 投递申请
     * @return "SUCCESS" | "DUPLICATE" | "ERROR"
     */
    public static String applyForJob(String taId, String jobId, String moId,
                                     String name, String major, String intro,
                                     String skills, String email, String cvPath) {
        if (APP_FILE_PATH == null) return "ERROR";
        if (hasActiveApplication(taId, jobId)) return "DUPLICATE";

        String appId = "APP" + System.currentTimeMillis();
        Application app = new Application(
                appId, name, jobId, moId, taId,
                major, intro, skills, email,
                cvPath == null ? "" : cvPath,
                "PENDING"
        );

        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        apps.add(app);
        CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, apps);
        return "SUCCESS";
    }

    /**
     * 撤回申请 — 仅允许 PENDING 状态
     * @return true 成功，false 失败（状态不对或不存在）
     */
    public static boolean withdrawApplication(String taId, String appId) {
        if (APP_FILE_PATH == null) return false;
        List<Application> apps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : apps) {
            if (appId.equals(app.getAppId()) && taId.equals(app.getTaId())) {
                if (!"PENDING".equals(app.getAppStatus())) return false;
                apps.remove(app);
                CsvFileUtil.writeAppListToCsv(APP_FILE_PATH, apps);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取该 TA 的所有申请历史
     */
    public static List<Application> getApplicationsByTA(String taId) {
        List<Application> result = new ArrayList<>();
        if (APP_FILE_PATH == null) return result;
        List<Application> allApps = CsvFileUtil.readAppListFromCsv(APP_FILE_PATH);
        for (Application app : allApps) {
            if (taId.equals(app.getTaId())) result.add(app);
        }
        return result;
    }

    /**
     * 根据 jobId 查找职位
     */
    public static Job getJobById(String jobId) {
        if (JOB_FILE_PATH == null) return null;
        List<Job> jobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        for (Job job : jobs) {
            if (jobId.equals(job.getJobId())) return job;
        }
        return null;
    }
}
