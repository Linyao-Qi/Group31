package com.tajobsystem.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    /** Splits a CSV line, respecting double-quoted fields that may contain commas. */
    public static String[] splitLine(String line) {
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

    /**
     * Wraps a field in double quotes, escaping any embedded double quotes.
     * Returns empty quoted string for null values.
     */
    public static String quoteField(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /**
     * Reads all data lines from a CSV file, skipping the header row.
     * Returns an empty list if the file does not exist (creates the file and its parent dirs).
     */
    public static List<String> readAllLines(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (!line.isBlank()) lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Writes a header row followed by data lines to a CSV file.
     * Creates parent directories if they do not exist.
     */
    public static void writeAllLines(String path, String header, List<String> lines) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(header);
            bw.newLine();
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
