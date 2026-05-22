package Admin;

import java.util.List;


public class AdminPostDomainService {

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

