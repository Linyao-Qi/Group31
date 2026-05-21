package TaJob;

/**
 * Lightweight immutable snapshot of TA profile data used by the recommendation service.
 *
 * @author Linyao Qi
 * @version 3
 */
public class TaProfileSnapshot {
    private final String taId;
    private final String name;
    private final String email;
    private final String skills;
    private final String major;

    /**
     * Creates a profile snapshot. Null values are normalised to empty strings.
     *
     * @param taId TA identifier
     * @param name TA name
     * @param email TA email address
     * @param skills comma-separated TA skills
     * @param major TA major
     */
    public TaProfileSnapshot(String taId, String name, String email, String skills, String major) {
        this.taId = nullToEmpty(taId);
        this.name = nullToEmpty(name);
        this.email = nullToEmpty(email);
        this.skills = nullToEmpty(skills);
        this.major = nullToEmpty(major);
    }

    /**
     * @return TA identifier
     */
    public String getTaId() {
        return taId;
    }

    /**
     * @return TA name
     */
    public String getName() {
        return name;
    }

    /**
     * @return TA email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return comma-separated TA skills
     */
    public String getSkills() {
        return skills;
    }

    /**
     * @return TA major
     */
    public String getMajor() {
        return major;
    }

    /**
     * Normalises nullable profile fields.
     *
     * @param value raw profile value
     * @return trimmed value, or an empty string when null
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
