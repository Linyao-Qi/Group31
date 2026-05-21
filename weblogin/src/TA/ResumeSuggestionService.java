package TA;

import com.SkillMatchUtil;
import com.SkillMatchUtil.MatchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Resume Suggestion Service
 * <p>Creates skill-based and content-based improvement suggestions for a TA's
 * resume. The service compares job requirements with TA skills and checks the
 * CV text for useful evidence such as GitHub links, projects, and teaching
 * experience.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class ResumeSuggestionService {

    /**
     * Generates resume improvement feedback for a TA application.
     * @param jobSkills comma-separated skill requirements from a job posting
     * @param taSkills comma-separated skills from the TA profile
     * @param cvText extracted or representative CV text
     * @return resume suggestion result containing score, matched skills, missing skills, and advice
     */
    public ResumeSuggestion generateSuggestion(
            String jobSkills,
            String taSkills,
            String cvText
    ) {

        /*
         * Skill matching
         */

        MatchResult result =
                SkillMatchUtil.calculateMatchResult(
                        jobSkills,
                        taSkills
                );

        /*
         * Generate suggestions
         */

        List<String> suggestions =
                new ArrayList<>();

        /*
         * Missing skills
         */

        for (String skill :
                result.getMissingSkills()) {

            suggestions.add(
                    "Consider improving your "
                            + skill
                            + " skill."
            );
        }

        /*
         * CV analysis
         */

        if (cvText == null) {
            cvText = "";
        }

        String lowerCv =
                cvText.toLowerCase();

        /*
         * GitHub suggestion
         */

        if (!lowerCv.contains("github")) {

            suggestions.add(
                    "Add your GitHub profile link."
            );
        }

        /*
         * Project experience
         */

        if (!lowerCv.contains("project")) {

            suggestions.add(
                    "Add more project experience."
            );
        }

        /*
         * Teaching experience
         */

        if (!lowerCv.contains("teaching")
                &&
                !lowerCv.contains("tutor")) {

            suggestions.add(
                    "Include teaching or tutoring experience."
            );
        }

        /*
         * Positive feedback
         */

        if (suggestions.isEmpty()) {

            suggestions.add(
                    "Your CV matches the job requirements well."
            );
        }

        /*
         * Return result
         */

        return new ResumeSuggestion(
                result.getScore(),
                result.getMatchedSkills(),
                result.getMissingSkills(),
                suggestions
        );
    }
}
