package service;

import model.Profile;
import java.io.*;

public class ProfileService {

    private static final String FILE_PATH = "data/profile.txt";

    public static void saveProfile(Profile profile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
        writer.write(profile.getName() + "," +
                     profile.getId() + "," +
                     profile.getEmail() + "," +
                     profile.getSkills() + "," +
                     profile.getMajor() + "," +
                     profile.getCvPath());
        writer.close();
    }

    public static Profile loadProfile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String line = reader.readLine();
        reader.close();

        if (line == null) return null;

        String[] data = line.split(",");
        return new Profile(data[0], data[1], data[2], data[3], data[4], data[5]);
    }
}