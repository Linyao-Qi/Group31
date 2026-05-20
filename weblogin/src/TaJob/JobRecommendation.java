package TaJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data model containing one recommended job and the explanation shown to the TA.
 *
 * @author Linyao Qi
 * @version 3
 */
public class JobRecommendation {
    private final JobPosting job;
    private final int score;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String reason;

    /**
     * Creates a recommendation result for one job posting.
     *
     * @param job recommended job
     * @param score match rate from 0 to 100
     * @param matchedSkills skills found in both the TA profile and job requirement
     * @param missingSkills required skills not currently listed in the TA profile
     * @param reason human-readable explanation of the recommendation
     */
    public JobRecommendation(JobPosting job, int score, List<String> matchedSkills,
                             List<String> missingSkills, String reason) {
        this.job = job;
        this.score = score;
        this.matchedSkills = matchedSkills == null ? Collections.emptyList() : new ArrayList<>(matchedSkills);
        this.missingSkills = missingSkills == null ? Collections.emptyList() : new ArrayList<>(missingSkills);
        this.reason = reason == null ? "" : reason;
    }

    /**
     * @return recommended job posting
     */
    public JobPosting getJob() {
        return job;
    }

    /**
     * @return match rate from 0 to 100
     */
    public int getScore() {
        return score;
    }

    /**
     * @return unmodifiable list of matched skills
     */
    public List<String> getMatchedSkills() {
        return Collections.unmodifiableList(matchedSkills);
    }

    /**
     * @return unmodifiable list of skills to confirm in the TA profile
     */
    public List<String> getMissingSkills() {
        return Collections.unmodifiableList(missingSkills);
    }

    /**
     * @return explanation shown with the recommendation result
     */
    public String getReason() {
        return reason;
    }
}
