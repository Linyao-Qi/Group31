package com;

import java.util.ArrayList;
import java.util.List;

public class SkillMatchUtil {

    // 计算单个申请的技能匹配分
    public static int calculateMatchScore(String jobReq, String taSkills) {
        // 空值处理
        if (jobReq == null || jobReq.isBlank() || taSkills == null || taSkills.isBlank()) {
            return 0;
        }

        String[] reqSkills = jobReq.split(",");
        String[] taSkillList = taSkills.split(",");
        int matchCount = 0;

        // 匹配技能（忽略大小写和空格）
        for (String req : reqSkills) {
            String trimReq = req.trim().toLowerCase();
            for (String ta : taSkillList) {
                String trimTa = ta.trim().toLowerCase();
                if (trimReq.equals(trimTa)) {
                    matchCount++;
                    break; // 匹配到即停止当前技能的遍历
                }
            }
        }

        // 避免除以0
        if (reqSkills.length == 0) {
            return 0;
        }

        // 计算百分比（四舍五入）
        return (int) Math.round((double) matchCount / reqSkills.length * 100);
    }

    // 批量计算匹配分（兼容原有调用，可选保留）
    public static List<Integer> calculateMatchScores(List<Application> apps, MoApplicantReviewService service) {
        List<Integer> scoreList = new ArrayList<>();
        for (Application app : apps) {
            String jobReq = service.getJobReq(app.getJobId());
            String taSkill = service.getTaSkill(app.getTaId());
            scoreList.add(calculateMatchScore(jobReq, taSkill));
        }
        return scoreList;
    }
}