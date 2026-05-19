package com;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SkillMatchUtil {

    
    public static int calculateMatchScore(String jobSkillRequirement, String taSkills) {
        return calculateMatchResult(jobSkillRequirement, taSkills).getScore();
    }

    public static MatchResult calculateMatchResult(String jobSkillRequirement, String taSkills) {
        List<String> requiredSkills = splitSkills(jobSkillRequirement);
        Set<String> taSkillSet = new LinkedHashSet<>();
        for (String skill : splitSkills(taSkills)) {
            taSkillSet.add(normalize(skill));
        }

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String requiredSkill : requiredSkills) {
            if (taSkillSet.contains(normalize(requiredSkill))) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }

        int score = requiredSkills.isEmpty()
                ? 0
                : (int) Math.round((double) matchedSkills.size() / requiredSkills.size() * 100);
        return new MatchResult(score, matchedSkills, missingSkills);
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

    private static List<String> splitSkills(String skills) {
        List<String> skillList = new ArrayList<>();
        if (skills == null || skills.isBlank()) {
            return skillList;
        }

        for (String skill : skills.split(",")) {
            String cleaned = skill.trim();
            if (!cleaned.isEmpty()) {
                skillList.add(cleaned);
            }
        }
        return skillList;
    }

    private static String normalize(String skill) {
        return skill == null ? "" : skill.trim().toLowerCase();
    }

    public static class MatchResult {
        private final int score;
        private final List<String> matchedSkills;
        private final List<String> missingSkills;

        public MatchResult(int score, List<String> matchedSkills, List<String> missingSkills) {
            this.score = score;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
        }

        public int getScore() {
            return score;
        }

        public List<String> getMatchedSkills() {
            return matchedSkills;
        }

        public List<String> getMissingSkills() {
            return missingSkills;
        }
    }
}
