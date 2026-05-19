package TaJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobRecommendation {
    private final JobPosting job;
    private final int score;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String reason;

    public JobRecommendation(JobPosting job, int score, List<String> matchedSkills,
                             List<String> missingSkills, String reason) {
        this.job = job;
        this.score = score;
        this.matchedSkills = matchedSkills == null ? Collections.emptyList() : new ArrayList<>(matchedSkills);
        this.missingSkills = missingSkills == null ? Collections.emptyList() : new ArrayList<>(missingSkills);
        this.reason = reason == null ? "" : reason;
    }

    public JobPosting getJob() {
        return job;
    }

    public int getScore() {
        return score;
    }

    public List<String> getMatchedSkills() {
        return Collections.unmodifiableList(matchedSkills);
    }

    public List<String> getMissingSkills() {
        return Collections.unmodifiableList(missingSkills);
    }

    public String getReason() {
        return reason;
    }
}
