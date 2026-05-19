package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV Utility Class for TA User Data Processing
 * <p>Provides specialized CSV reading and parsing functions for TA profile data,
 * including skill extraction and TA ID list retrieval.
 * Supports standard CSV format with quote escaping and field parsing logic.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-04-15
 */
public class TaCsvUtil {

    /**
     * Load TA ID and corresponding skill information from CSV file
     * @param filePath path to the TA profile CSV file
     * @return Map containing TA ID as key and skill string as value
     */
    public static Map<String, String> loadTaSkills(String filePath) {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (first || line.isBlank()) {
                    first = false;
                    continue;
                }
                String[] arr = parseCsvLine(line);
                if (arr.length < 4) {
                    continue;
                }
                String taId = arr[0].trim();
                map.put(taId, cleanCsvValue(arr[3]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Retrieve all valid TA user IDs from the CSV file
     * @param filePath path to the TA profile CSV file
     * @return List of TA IDs
     */
    public static List<String> getAllTaIds(String filePath) {
        List<String> ids = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (first || line.isBlank()) {
                    first = false;
                    continue;
                }
                String[] arr = parseCsvLine(line);
                if (arr.length >= 1 && !arr[0].trim().isBlank()) {
                    ids.add(cleanCsvValue(arr[0]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    /**
     * Parse a single CSV line with support for quoted fields and commas inside values
     * @param line raw CSV line string
     * @return String array of parsed and separated fields
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Clean and trim CSV field value, return empty string if null
     * @param value raw field value
     * @return cleaned and trimmed string
     */
    private static String cleanCsvValue(String value) {
        return value == null ? "" : value.trim();
    }
}
