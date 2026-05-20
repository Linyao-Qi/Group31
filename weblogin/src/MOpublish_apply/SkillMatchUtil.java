package com;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for comparing TA skills against job skill requirements.
 * <p>Provides methods to calculate percentage match scores and detailed
 * matched/missing skill lists for MO applicant review pages.</p>
 *
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class SkillMatchUtil {

    /**
     * Calculate only the numeric match score between a job and a TA skill list.
     *
     * @param jobSkillRequirement comma-separated skills required by the job
     * @param taSkills comma-separated skills owned by the TA
     * @return percentage match score from 0 to 100
     */
    public static int calculateMatchScore(String jobSkillRequirement, String taSkills) {
        return calculateMatchResult(jobSkillRequirement, taSkills).getScore();
    }

    /**
     * Calculate detailed skill matching result for a job requirement and TA skills.
     *
     * @param jobSkillRequirement comma-separated skills required by the job
     * @param taSkills comma-separated skills owned by the TA
     * @return MatchResult containing score, matched skills, and missing skills
     */
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

    /**
     * Calculate match scores for a list of applications.
     *
     * @param apps applications to evaluate
     * @param service review service used to load job and TA skill data
     * @return list of match scores in the same order as the input applications
     */
    public static List<Integer> calculateMatchScores(List<Application> apps, MoApplicantReviewService service) {
        List<Integer> scoreList = new ArrayList<>();
        for (Application app : apps) {
            
            String jobSkillRequirement = service.getSkillRequirement(app.getJobId());
            String taSkill = service.getTaSkill(app.getTaId());
            scoreList.add(calculateMatchScore(jobSkillRequirement, taSkill));
        }
        return scoreList;
    }

    /**
     * Split a comma-separated skill string into cleaned skill names.
     *
     * @param skills comma-separated skill text
     * @return list of non-empty skill names
     */
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

    /**
     * Normalize a skill value for case-insensitive comparison.
     *
     * @param skill original skill value
     * @return trimmed lowercase skill value, or empty string for null
     */
    private static String normalize(String skill) {
        return skill == null ? "" : skill.trim().toLowerCase();
    }

    /**
     * Result object for detailed skill matching.
     */
    public static class MatchResult {

        /** Percentage score for matched required skills */
        private final int score;

        /** Required skills that the TA has */
        private final List<String> matchedSkills;

        /** Required skills that the TA is missing */
        private final List<String> missingSkills;

        /**
         * Create a skill match result.
         *
         * @param score percentage match score
         * @param matchedSkills required skills found in the TA profile
         * @param missingSkills required skills not found in the TA profile
         */
        public MatchResult(int score, List<String> matchedSkills, List<String> missingSkills) {
            this.score = score;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
        }

        /**
         * Get the percentage match score.
         *
         * @return match score from 0 to 100
         */
        public int getScore() {
            return score;
        }

        /**
         * Get the required skills matched by the TA.
         *
         * @return list of matched skills
         */
        public List<String> getMatchedSkills() {
            return matchedSkills;
        }

        /**
         * Get the required skills missing from the TA profile.
         *
         * @return list of missing skills
         */
        public List<String> getMissingSkills() {
            return missingSkills;
        }
    }
}
