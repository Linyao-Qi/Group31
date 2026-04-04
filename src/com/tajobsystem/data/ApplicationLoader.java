package com.tajobsystem.data;

import com.tajobsystem.model.Application;

import java.util.ArrayList;
import java.util.List;

public class ApplicationLoader {

    private static final String HEADER = "appId,jobId,taId,appStatus,cvFilePath";

    public static List<Application> loadApplicationsFromCSV(String path) {
        List<Application> apps = new ArrayList<>();
        for (String line : CsvUtil.readAllLines(path)) {
            String[] parts = CsvUtil.splitLine(line);
            if (parts.length < 5) continue;
            apps.add(new Application(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim()
            ));
        }
        return apps;
    }

    public static void writeApplicationsToCSV(String path, List<Application> apps) {
        List<String> lines = new ArrayList<>();
        for (Application a : apps) {
            lines.add(
                    a.getAppId() + "," +
                    a.getJobId() + "," +
                    a.getTaId() + "," +
                    a.getAppStatus() + "," +
                    CsvUtil.quoteField(a.getCvFilePath())
            );
        }
        CsvUtil.writeAllLines(path, HEADER, lines);
    }
}
