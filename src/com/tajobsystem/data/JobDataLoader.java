package com.tajobsystem.data;

import com.tajobsystem.model.Job;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JobDataLoader {

    public static List<Job> loadJobsFromCSV(String filePath) {
        List<Job> jobs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length < 12) {
                    continue;
                }

                Job job = new Job(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        parts[5],
                        parts[6],
                        Integer.parseInt(parts[7]),
                        parts[8],
                        parts[9],
                        parts[10],
                        Boolean.parseBoolean(parts[11])
                );

                // Optional moId column (index 12)
                if (parts.length >= 13 && !parts[12].trim().isEmpty()) {
                    job.setMoId(parts[12].trim());
                }

                jobs.add(job);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jobs;
    }

    /** Loads MO-published jobs from the binary dat file. */
    public static List<Job> loadJobsFromDat(String filePath) {
        return FileUtil.read(filePath);
    }
}