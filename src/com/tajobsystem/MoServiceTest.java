package com.tajobsystem;

import com.tajobsystem.data.FileUtil;
import com.tajobsystem.model.Application;
import com.tajobsystem.model.Job;
import com.tajobsystem.service.MoService;

import java.util.ArrayList;
import java.util.List;

public class MoServiceTest {
    public static void main(String[] args) {
        MoService moService = new MoService();
        String moId = "MO001";

        Job job = moService.publishJob(moId, "Java TA", "会Java");
        if (job == null) return;

        String appId = "APP001";
        Application app = new Application(appId, job.getJobId(), "TA001", "Submitted");
        List<Application> apps = new ArrayList<>();
        apps.add(app);
        FileUtil.write("./data/app.dat", apps);
        System.out.println("已创建测试申请：" + app);

        System.out.println("=== 开始录用 ===");
        moService.acceptApplicant(moId, appId);

        System.out.println("=== 无权限测试 ===");
        moService.acceptApplicant("MO999", appId);
    }
}
