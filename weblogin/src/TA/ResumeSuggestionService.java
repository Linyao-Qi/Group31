package TA;

import com.SkillMatchUtil;
import com.SkillMatchUtil.MatchResult;

import java.util.ArrayList;
import java.util.List;

public class ResumeSuggestionService {

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