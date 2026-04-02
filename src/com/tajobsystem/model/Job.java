package com.tajobsystem.model;

public class Job {
    private String jobId;
    private String title;
    private String subject;
    private String workType;
    private String department;
    private String description;
    private String requirements;
    private int openPositions;
    private String deadline;
    private String hoursPerWeek;
    private String compensation;
    private boolean open;

    public Job(String jobId, String title, String subject, String workType,
               String department, String description, String requirements,
               int openPositions, String deadline, String hoursPerWeek,
               String compensation, boolean open) {
        this.jobId = jobId;
        this.title = title;
        this.subject = subject;
        this.workType = workType;
        this.department = department;
        this.description = description;
        this.requirements = requirements;
        this.openPositions = openPositions;
        this.deadline = deadline;
        this.hoursPerWeek = hoursPerWeek;
        this.compensation = compensation;
        this.open = open;
    }

    public String getJobId() {
        return jobId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getWorkType() {
        return workType;
    }

    public String getDepartment() {
        return department;
    }

    public String getDescription() {
        return description;
    }

    public String getRequirements() {
        return requirements;
    }

    public int getOpenPositions() {
        return openPositions;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getHoursPerWeek() {
        return hoursPerWeek;
    }

    public String getCompensation() {
        return compensation;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public String toString() {
        return title + " | " + subject + " | " + workType + " | Deadline: " + deadline;
    }
}