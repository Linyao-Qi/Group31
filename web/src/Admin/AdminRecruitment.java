package Admin;


public class AdminRecruitment {
    private String jobId;
    private String title;
    private String subject;
    private String workType;
    private String department;
    private String description;
    private String requirements;
    private int openPositions;
    private String deadline;
    private double hoursPerWeek;
    private String compensation;
    private boolean open;
    private String moId;

    public AdminRecruitment(
            String jobId,
            String title,
            String subject,
            String workType,
            String department,
            String description,
            String requirements,
            int openPositions,
            String deadline,
            double hoursPerWeek,
            String compensation,
            boolean open,
            String moId
    ) {
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
        this.moId = moId;
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

    public double getHoursPerWeek() {
        return hoursPerWeek;
    }

    public String getCompensation() {
        return compensation;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getMoId() {
        return moId;
    }

    public String getDisplayStatus() {

        return open ? "Open" : "Closed";
    }
}

