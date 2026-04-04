package com.tajobsystem.data;

import com.tajobsystem.model.TAProfile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TAProfileLoader {

    public static List<TAProfile> loadProfilesFromCSV(String filePath) {
        List<TAProfile> profiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }

                // Handle quoted fields (e.g. "Java, Python, ...")
                String[] parts = splitCSVLine(line);
                if (parts.length < 4) continue;

                profiles.add(new TAProfile(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return profiles;
    }

    /** Splits a CSV line, respecting double-quoted fields that may contain commas. */
    private static String[] splitCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}
