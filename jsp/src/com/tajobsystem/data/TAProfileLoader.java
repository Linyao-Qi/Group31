package com.tajobsystem.data;

import com.tajobsystem.model.TAProfile;

import java.util.ArrayList;
import java.util.List;

public class TAProfileLoader {

    private static final String HEADER = "taId,name,email,skills";

    public static List<TAProfile> loadProfilesFromCSV(String filePath) {
        List<TAProfile> profiles = new ArrayList<>();
        for (String line : CsvUtil.readAllLines(filePath)) {
            String[] parts = CsvUtil.splitLine(line);
            if (parts.length < 4) continue;
            profiles.add(new TAProfile(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim()
            ));
        }
        return profiles;
    }

    public static void writeProfilesToCSV(String path, List<TAProfile> profiles) {
        List<String> lines = new ArrayList<>();
        for (TAProfile p : profiles) {
            lines.add(
                    p.getTaId() + "," +
                    CsvUtil.quoteField(p.getName()) + "," +
                    CsvUtil.quoteField(p.getEmail()) + "," +
                    CsvUtil.quoteField(p.getSkills())
            );
        }
        CsvUtil.writeAllLines(path, HEADER, lines);
    }
}
