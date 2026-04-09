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
                String[] arr = line.split(",");
                if (arr.length < 4) {
                    continue; 
                }
                String taId = arr[0].trim();
                
                StringBuilder skills = new StringBuilder();
                for (int i = 3; i < arr.length; i++) {
                    skills.append(arr[i].trim());
                    if (i < arr.length - 1) {
                        skills.append(",");
                    }
                }
                map.put(taId, skills.toString());
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
                String[] arr = line.split(",");
                if (arr.length >= 1 && !arr[0].trim().isBlank()) {
                    ids.add(arr[0].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }
}