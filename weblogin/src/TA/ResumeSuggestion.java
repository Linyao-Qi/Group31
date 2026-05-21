package TA;

import java.util.List;

/**
 * Resume Suggestion Entity Class
 * <p>Stores the result of AI-assisted resume evaluation. It contains the
 * matching score, matched skills, missing skills, and personalized improvement
 * suggestions shown to the teaching assistant.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class ResumeSuggestion {

    /** Total matching score between TA skills and job requirements, from 0 to 100 */
    private final int score;

    /** Skills that the TA possesses and that match the job requirements */
    private final List<String> matchedSkills;

    /** Important job skills that are missing from the TA profile */
    private final List<String> missingSkills;

    /** Personalized suggestions for improving the TA resume */
    private final List<String> suggestions;

    /**
     * Full constructor to create a resume suggestion result.
     * @param score overall matching score
     * @param matchedSkills skills that meet job requirements
     * @param missingSkills skills lacking in the TA's profile
     * @param suggestions resume improvement advice
     */
    public ResumeSuggestion(
            int score,
            List<String> matchedSkills,
            List<String> missingSkills,
            List<String> suggestions
    ) {
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
    }

    /**
     * Gets the overall skill matching score.
     * @return score from 0 to 100
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the skills matched between the TA profile and job requirements.
     * @return matched skill list
     */
    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    /**
     * Gets the skills required by the job but missing from the TA profile.
     * @return missing skill list
     */
    public List<String> getMissingSkills() {
        return missingSkills;
    }

    /**
     * Gets personalized resume improvement suggestions.
     * @return suggestion list
     */
    public List<String> getSuggestions() {
        return suggestions;
    }
}
