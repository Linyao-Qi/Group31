package TA;

import java.util.List;

public class ResumeSuggestion {

    private final int score;

    private final List<String> matchedSkills;

    private final List<String> missingSkills;

    private final List<String> suggestions;

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