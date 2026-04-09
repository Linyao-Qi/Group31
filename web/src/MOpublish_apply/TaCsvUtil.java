package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取 profiles.csv
 * Schema: taId,name,email,skills,major,cvPath
 * skills 字段可能被双引号包裹（含逗号时）
 */
public class TaCsvUtil {

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
                if (arr.length < 4) continue;
                String taId = arr[0].trim();
                // index 3 = skills (single quoted field)
                String skills = arr[3].trim();
                map.put(taId, skills);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

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
                    ids.add(arr[0].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    // Quote-aware CSV line parser (handles "field,with,commas")
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }
}
