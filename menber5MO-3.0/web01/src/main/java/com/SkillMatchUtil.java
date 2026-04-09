package com;

import java.util.ArrayList;
import java.util.List;

public class SkillMatchUtil {

    
    public static int calculateMatchScore(String jobSkillRequirement, String taSkills) {
        
        if (jobSkillRequirement == null || jobSkillRequirement.isBlank() || taSkills == null || taSkills.isBlank()) {
            return 0;
        }

        
        String[] reqSkills = jobSkillRequirement.split(",");
        String[] taSkillList = taSkills.split(",");
        int matchCount = 0;

        
        for (String req : reqSkills) {
            String trimReq = req.trim().toLowerCase();
            for (String ta : taSkillList) {
                String trimTa = ta.trim().toLowerCase();
                if (trimReq.equals(trimTa)) {
                    matchCount++;
                    break;
                }
            }
        }

        
        if (reqSkills.length == 0) {
            return 0;
        }

        
        return (int) Math.round((double) matchCount / reqSkills.length * 100);
    }

  
    public static List<Integer> calculateMatchScores(List<Application> apps, MoApplicantReviewService service) {
        List<Integer> scoreList = new ArrayList<>();
        for (Application app : apps) {
            
            String jobSkillRequirement = service.getSkillRequirement(app.getJobId());
            String taSkill = service.getTaSkill(app.getTaId());
            scoreList.add(calculateMatchScore(jobSkillRequirement, taSkill));
        }
        return scoreList;
    }
}