package com.tajobsystem.service;

import com.tajobsystem.model.Job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JobService {

    public List<Job> filterJobs(List<Job> jobs, String keyword, String subject, String workType) {
        List<Job> result = new ArrayList<Job>();

        for (Job job : jobs) {
            if (!job.isOpen()) {
                continue;
            }

            boolean matchesKeyword = (keyword == null || keyword.trim().isEmpty())
                    || job.getTitle().toLowerCase().contains(keyword.toLowerCase())
                    || job.getSubject().toLowerCase().contains(keyword.toLowerCase());

            boolean matchesSubject = (subject == null || subject.equals("All Subjects"))
                    || job.getSubject().equalsIgnoreCase(subject);

            boolean matchesWorkType = (workType == null || workType.equals("All Types"))
                    || job.getWorkType().equalsIgnoreCase(workType);

            if (matchesKeyword && matchesSubject && matchesWorkType) {
                result.add(job);
            }
        }

        return result;
    }

    public void sortJobs(List<Job> jobs, String sortBy) {
        if (sortBy == null) {
            return;
        }

        if (sortBy.equals("Deadline")) {
            Collections.sort(jobs, new Comparator<Job>() {
                @Override
                public int compare(Job j1, Job j2) {
                    return j1.getDeadline().compareTo(j2.getDeadline());
                }
            });
        } else if (sortBy.equals("Title")) {
            Collections.sort(jobs, new Comparator<Job>() {
                @Override
                public int compare(Job j1, Job j2) {
                    return j1.getTitle().compareToIgnoreCase(j2.getTitle());
                }
            });
        }
    }
}