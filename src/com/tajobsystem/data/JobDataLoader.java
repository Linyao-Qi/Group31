package com.tajobsystem.data;

import com.tajobsystem.model.Job;

import java.util.ArrayList;
import java.util.List;

public class JobDataLoader {

    private static final String HEADER =
            "jobId,title,subject,workType,department,description,requirements," +
            "openPositions,deadline,hoursPerWeek,compensation,open,moId,jobStatus";

    public static List<Job> loadJobsFromCSV(String filePath) {
        List<Job> jobs = new ArrayList<>();
        for (String line : CsvUtil.readAllLines(filePath)) {
            String[] parts = CsvUtil.splitLine(line);
            if (parts.length < 12) continue;

            Job job = new Job(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim(),
                    parts[6].trim(),
                    Integer.parseInt(parts[7].trim()),
                    parts[8].trim(),
                    parts[9].trim(),
                    parts[10].trim(),
                    Boolean.parseBoolean(parts[11].trim())
            );

// Optional moId column (index 12)
if (parts.length >= 13 && !parts[12].trim().isEmpty()) {
    job.setMoId(parts[12].trim());
}
// Optional jobStatus column (index 13)
if (parts.length >= 14 && !parts[13].trim().isEmpty()) {
    job.setJobStatus(parts[13].trim());
}

jobs.add(job);
main
            }

            jobs.add(job);
        }
        return jobs;
    }

    public static void writeJobsToCSV(String path, List<Job> jobs) {
        List<String> lines = new ArrayList<>();
        for (Job j : jobs) {
            lines.add(
                    j.getJobId() + "," +
                    CsvUtil.quoteField(j.getTitle()) + "," +
                    CsvUtil.quoteField(j.getSubject()) + "," +
                    CsvUtil.quoteField(j.getWorkType()) + "," +
                    CsvUtil.quoteField(j.getDepartment()) + "," +
                    CsvUtil.quoteField(j.getDescription()) + "," +
                    CsvUtil.quoteField(j.getRequirements()) + "," +
                    j.getOpenPositions() + "," +
                    CsvUtil.quoteField(j.getDeadline()) + "," +
                    CsvUtil.quoteField(j.getHoursPerWeek()) + "," +
                    CsvUtil.quoteField(j.getCompensation()) + "," +
                    j.isOpen() + "," +
                    CsvUtil.quoteField(j.getMoId()) + "," +
                    CsvUtil.quoteField(j.getJobStatus())
            );
        }
        CsvUtil.writeAllLines(path, HEADER, lines);
    }
}
