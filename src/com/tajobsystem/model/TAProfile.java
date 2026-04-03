package com.tajobsystem.model;

import java.io.Serializable;

public class TAProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taId;
    private String name;
    private String email;
    private String skills;      // e.g. "Java, Python, Data Structures"

    public TAProfile() {}

    public TAProfile(String taId, String name, String email, String skills) {
        this.taId = taId;
        this.name = name;
        this.email = email;
        this.skills = skills;
    }

    public String getTaId()    { return taId; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getSkills()  { return skills; }

    public void setTaId(String taId)       { this.taId = taId; }
    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setSkills(String skills)   { this.skills = skills; }

    @Override
    public String toString() {
        return name + " (" + taId + ")";
    }
}
