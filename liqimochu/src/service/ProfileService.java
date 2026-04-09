package service;

import model.Profile;
import java.io.*;

public class ProfileService {

    // 固定存储路径
    private static final String DIR_PATH =
            System.getProperty("user.home") + "/Group31/liqimochu/data";

    private static final String FILE_PATH =
            DIR_PATH + "/profile.txt";

    public static void saveProfile(Profile profile) throws IOException {

        File dir = new File(DIR_PATH);
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            writer.write(profile.getName() + "," +
                    profile.getId() + "," +
                    profile.getEmail() + "," +
                    profile.getSkills() + "," +
                    profile.getMajor() + "," +
                    profile.getCvPath());
        }
    }

    public static Profile loadProfile() throws IOException {

        File file = new File(FILE_PATH);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();
            if (line == null) return null;

            String[] data = line.split(",");
            if (data.length < 6) return null;

            return new Profile(data[0], data[1], data[2],
                    data[3], data[4], data[5]);
        }
    }
}