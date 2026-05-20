package TA;

/**
 * Entity class for TA personal profile data model
 * Stores all basic information and CV file path of a teaching assistant
 */
public class TAProfile {
    private String taId;
    private String name;
    private String email;
    private String skills;
    private String major;
    private String cvPath;

    /**
     * Default no-argument constructor
     */
    public TAProfile() {}

    /**
     * Full parameterized constructor for initializing complete profile info
     * @param taId Unique TA ID
     * @param name TA full name
     * @param email Contact email
     * @param skills Personal professional skills
     * @param major Academic major
     * @param cvPath Storage path of resume file
     */
    public TAProfile(String taId, String name, String email,
                     String skills, String major, String cvPath) {
        this.taId = taId;
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.major = major;
        this.cvPath = cvPath;
    }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }
}
