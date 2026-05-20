package TA;

/**
 * TA Profile Entity Class
 * <p>Stores teaching assistant personal profile information, including basic
 * identity fields, contact details, skills, academic major, and the stored CV
 * file path.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class TAProfile {
    /** Unique teaching assistant identifier */
    private String taId;

    /** Teaching assistant full name */
    private String name;

    /** Teaching assistant contact email */
    private String email;

    /** Comma-separated skill list */
    private String skills;

    /** Teaching assistant academic major */
    private String major;

    /** Stored CV file path relative to the data directory */
    private String cvPath;

    /**
     * Default no-argument constructor.
     */
    public TAProfile() {}

    /**
     * Full parameterized constructor for initializing complete profile information.
     * @param taId unique TA ID
     * @param name TA full name
     * @param email contact email
     * @param skills personal professional skills
     * @param major academic major
     * @param cvPath storage path of the resume file
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

    /**
     * Gets the TA identifier.
     * @return TA identifier
     */
    public String getTaId() { return taId; }

    /**
     * Sets the TA identifier.
     * @param taId TA identifier
     */
    public void setTaId(String taId) { this.taId = taId; }

    /**
     * Gets the TA full name.
     * @return TA full name
     */
    public String getName() { return name; }

    /**
     * Sets the TA full name.
     * @param name TA full name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the TA contact email.
     * @return contact email
     */
    public String getEmail() { return email; }

    /**
     * Sets the TA contact email.
     * @param email contact email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the TA skill list.
     * @return skill list
     */
    public String getSkills() { return skills; }

    /**
     * Sets the TA skill list.
     * @param skills skill list
     */
    public void setSkills(String skills) { this.skills = skills; }

    /**
     * Gets the TA academic major.
     * @return academic major
     */
    public String getMajor() { return major; }

    /**
     * Sets the TA academic major.
     * @param major academic major
     */
    public void setMajor(String major) { this.major = major; }

    /**
     * Gets the stored CV path.
     * @return stored CV path
     */
    public String getCvPath() { return cvPath; }

    /**
     * Sets the stored CV path.
     * @param cvPath stored CV path
     */
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }
}
