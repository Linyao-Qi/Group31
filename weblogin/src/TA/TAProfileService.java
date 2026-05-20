package TA;

import jakarta.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TA Profile Service
 * <p>Reads and writes teaching assistant profile records stored in
 * data/profiles.csv. Each row follows the schema:
 * taId,name,email,skills,major,cvPath. The service also migrates newer
 * legacy profile data from ta_profiles.csv when available.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class TAProfileService {

    /** Main TA profile CSV path */
    private static String TA_PROFILE_PATH;

    /** Legacy profile CSV path used by older project versions */
    private static String LEGACY_TA_PROFILE_PATH;

    /**
     * Initializes profile CSV paths and migrates newer legacy data if needed.
     * @param context servlet context used to resolve data file locations
     */
    public static void init(ServletContext context) {
        TA_PROFILE_PATH = context.getRealPath("data/profiles.csv");
        LEGACY_TA_PROFILE_PATH = context.getRealPath("data/ta_profiles.csv");
        migrateLegacyProfileFileIfNewer();
    }

    /**
     * Finds a TA profile by TA identifier.
     * @param taId teaching assistant identifier
     * @return matching profile, or null if not found
     */
    public static TAProfile getProfileByTaId(String taId) {
        for (TAProfile p : loadAll()) {
            if (taId != null && taId.equals(p.getTaId())) return p;
        }
        return null;
    }

    /**
     * Saves a new profile or replaces the existing profile for the same TA.
     * @param profile profile record to persist
     */
    public static void saveOrUpdateProfile(TAProfile profile) {
        List<TAProfile> profiles = loadAll();
        boolean found = false;
        for (int i = 0; i < profiles.size(); i++) {
            if (profile.getTaId() != null && profile.getTaId().equals(profiles.get(i).getTaId())) {
                profiles.set(i, profile);
                found = true;
                break;
            }
        }
        if (!found) profiles.add(profile);
        writeAll(profiles);
    }

    /**
     * Loads all profiles from the main profile CSV file.
     * @return list of TA profiles
     */
    private static List<TAProfile> loadAll() {
        if (TA_PROFILE_PATH == null) return new ArrayList<>();
        return loadFromFile(new File(TA_PROFILE_PATH));
    }

    /**
     * Loads TA profiles from a specific CSV file.
     * @param file profile CSV file
     * @return list of parsed TA profiles
     */
    private static List<TAProfile> loadFromFile(File file) {
        List<TAProfile> list = new ArrayList<>();
        if (file == null || !file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.isBlank()) continue;
                String[] f = parseCsvLine(line);
                if (f.length < 4) continue;

                TAProfile p = new TAProfile();
                p.setTaId(f[0].trim());
                p.setName(f[1].trim());
                p.setEmail(f[2].trim());
                p.setSkills(f[3].trim());
                p.setMajor(f.length > 4 ? f[4].trim() : "");
                p.setCvPath(f.length > 5 ? f[5].trim() : "");
                list.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Writes all TA profile records to profiles.csv.
     * @param profiles profiles to persist
     */
    private static void writeAll(List<TAProfile> profiles) {
        if (TA_PROFILE_PATH == null) return;
        File file = new File(TA_PROFILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("taId,name,email,skills,major,cvPath");
            writer.newLine();
            for (TAProfile p : profiles) {
                writer.write(String.join(",",
                        safe(p.getTaId()),
                        safe(p.getName()),
                        safe(p.getEmail()),
                        escapeField(p.getSkills()),
                        escapeField(p.getMajor()),
                        safe(p.getCvPath())
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a CSV row while preserving commas inside quoted fields.
     * @param line raw CSV row
     * @return parsed fields
     */
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

    /**
     * Escapes a value for safe CSV output.
     * @param value raw field value
     * @return CSV-safe field value
     */
    private static String escapeField(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Converts null values to empty strings for CSV output.
     * @param value nullable value
     * @return original value or empty string
     */
    private static String safe(String value) {
        return value == null ? "" : value;
    }

    /**
     * Migrates profiles from ta_profiles.csv when that legacy file is newer
     * than the main profiles.csv file.
     */
    private static void migrateLegacyProfileFileIfNewer() {
        if (TA_PROFILE_PATH == null || LEGACY_TA_PROFILE_PATH == null) return;

        File mainFile = new File(TA_PROFILE_PATH);
        File legacyFile = new File(LEGACY_TA_PROFILE_PATH);
        if (!legacyFile.exists()) return;
        if (mainFile.exists() && legacyFile.lastModified() <= mainFile.lastModified()) return;

        List<TAProfile> merged = loadFromFile(mainFile);
        for (TAProfile legacyProfile : loadFromFile(legacyFile)) {
            boolean found = false;
            for (int i = 0; i < merged.size(); i++) {
                if (legacyProfile.getTaId() != null
                        && legacyProfile.getTaId().equals(merged.get(i).getTaId())) {
                    merged.set(i, legacyProfile);
                    found = true;
                    break;
                }
            }
            if (!found) merged.add(legacyProfile);
        }
        writeAll(merged);
    }
}
