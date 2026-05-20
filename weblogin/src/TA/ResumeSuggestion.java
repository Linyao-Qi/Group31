package TA;

import java.util.List;

/**
 * Data Model class that stores the result of AI-assisted resume evaluation.
 * It contains matching score, matched skills, missing skills, and improvement suggestions.
 */
public class ResumeSuggestion {

    // Total matching score between TA skills and job requirements (0-100)
    private final int score;

    // List of skills that the TA possesses and matches the job requirements
    private final List<String> matchedSkills;

    // List of important skills required by the job but missing from the TA's profile
    private final List<String> missingSkills;

    // Personalized suggestions for improving the TA's resume
    private final List<String> suggestions;

    /**
     * Full constructor to create a ResumeSuggestion object
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

    public int getScore() {
        return score;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }
}