package com;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class TaCsvUtil {
    // 动态加载所有TA的ID-技能映射（兼容技能中的空格，鲁棒性优化）
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
                } // 跳过表头/空行
                // 拆分：前3个字段是taId/name/email，剩余所有字段拼接为skills（兼容技能中的逗号/空格）
                String[] arr = line.split(",");
                if (arr.length < 4) {
                    continue; // 字段不全直接跳过
                }
                String taId = arr[0].trim();
                // 拼接第3列之后的所有内容为技能（解决技能含空格/逗号的问题）
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
    // 动态获取所有TA的ID列表（优化：跳过空行/无效行）
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
                } // 跳过表头/空行
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