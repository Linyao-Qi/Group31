package TA;

import jakarta.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TAAuthService {
    private static String authFilePath;

    public static void init(ServletContext context) {
        authFilePath = context.getRealPath("data/auth.csv");
    }

    public static boolean authenticateTA(String taId, String password) {
        if (taId == null || taId.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        if (authFilePath == null) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(authFilePath), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = parseCsvLine(line);
                if (fields.length < 3) {
                    continue;
                }
                if ("TA".equalsIgnoreCase(fields[0].trim())
                        && taId.equals(fields[1].trim())
                        && password.equals(fields[2].trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
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
}
