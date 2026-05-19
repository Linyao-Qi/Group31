package com;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static String cleanCsvValue(String value) {
        return value == null ? "" : value.trim();
    }
}
