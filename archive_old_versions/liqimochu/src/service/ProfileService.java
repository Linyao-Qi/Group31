package service;

import model.Profile;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileService {

    // 固定存储路径
    private static final String DIR_PATH =
            System.getProperty("user.home") + "/Group31/liqimochu/data";

    private static final String FILE_PATH =
            DIR_PATH + "/TAprofile.csv";

    public static void saveOrUpdate(Profile newProfile) throws IOException {
        List<Profile> list = getAllProfiles();

        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(newProfile.getId())) {
                list.set(i, newProfile);
                found = true;
                break;
            }
        }

        if (!found) list.add(newProfile);

        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("taId,name,email,skills,major,cvPath");
            writer.newLine();

            for (Profile p : list) {
                writer.write(p.getId() + "," +
                        p.getName() + "," +
                        p.getEmail() + "," +
                        p.getSkills() + "," +
                        p.getMajor() + "," +
                        p.getCvPath());
                writer.newLine();
            }
        }
    }

    public static List<Profile> getAllProfiles() throws IOException {
        List<Profile> list = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] d = line.split(",");
                if (d.length >= 6) {
                    list.add(new Profile(
                            d[1], d[0], d[2],
                            d[3], d[4], d[5]
                    ));
                }
            }
        }
        return list;
    }

    public static void deleteById(String id) throws IOException {
        List<Profile> list = getAllProfiles();
        list.removeIf(p -> p.getId().equals(id)); // 直接移除对应ID

        // 重新写入文件
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("taId,name,email,skills,major,cvPath");
            writer.newLine();
            for (Profile p : list) {
                writer.write(p.getId() + "," + p.getName() + "," + p.getEmail() + "," +
                        p.getSkills() + "," + p.getMajor() + "," + p.getCvPath());
                writer.newLine();
            }
        }
    }

}