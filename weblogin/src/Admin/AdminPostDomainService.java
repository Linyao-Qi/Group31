package Admin;

import java.util.List;


/**
 * Applies business rules for administrator recruitment-post changes.
 *
 * @author Yutong Yao
 * @version 1.0
 */
public class AdminPostDomainService {

    /**
     * Sets the open or closed state for a matching recruitment post.
     *
     * @param posts posts to search and update
     * @param jobId target job identifier
     * @param targetOpen desired open state
     * @return true when a matching post changed; false when no change was needed or no post matched
     */
    public boolean setOpenStatus(List<AdminRecruitment> posts, String jobId, boolean targetOpen) {
        String targetJobId = valueOrEmpty(jobId);
        for (AdminRecruitment post : posts) {
            if (!targetJobId.equals(valueOrEmpty(post.getJobId()))) {
                continue;
            }
            if (post.isOpen() == targetOpen) {
                return false;
            }
            post.setOpen(targetOpen);
            return true;
        }
        return false;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}

