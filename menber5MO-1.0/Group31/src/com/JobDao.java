package com;
import java.util.List;

public class JobDao {
	private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String JOB_FILE_PATH = PROJECT_PATH + "/data/job.csv";
    public static Job getJobById(String jobId) {
        
        List<Job> allJobs = CsvFileUtil.readJobListFromCsv(JOB_FILE_PATH);
        
        for (Job job : allJobs) {
            if (jobId != null && jobId.equals(job.getJobId())) {
                return job;
            }
        }
        return null;
    }
}