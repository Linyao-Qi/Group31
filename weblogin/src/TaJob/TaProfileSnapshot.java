package TaJob;

public class TaProfileSnapshot {
    private final String taId;
    private final String name;
    private final String email;
    private final String skills;
    private final String major;

    public TaProfileSnapshot(String taId, String name, String email, String skills, String major) {
        this.taId = nullToEmpty(taId);
        this.name = nullToEmpty(name);
        this.email = nullToEmpty(email);
        this.skills = nullToEmpty(skills);
        this.major = nullToEmpty(major);
    }

    public String getTaId() {
        return taId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSkills() {
        return skills;
    }

    public String getMajor() {
        return major;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
