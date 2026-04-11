package model;

public class Profile {
    private String name;
    private String id;
    private String email;
    private String skills;
    private String major;
    private String cvPath;

    public Profile(String name, String id, String email,
                   String skills, String major, String cvPath) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.skills = skills;
        this.major = major;
        this.cvPath = cvPath;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getSkills() { return skills; }
    public String getMajor() { return major; }
    public String getCvPath() { return cvPath; }

    public void setName(String name) { this.name = name; }
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setMajor(String major) { this.major = major; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }
}