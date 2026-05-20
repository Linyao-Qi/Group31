package Admin;


/**
 * Stores the administrator view of one recruitment post.
 *
 * @author Yutong Yao
 * @version 1.0
 */
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

    /**
     * Creates a recruitment post record.
     *
     * @param jobId unique job identifier
     * @param title post title
     * @param subject subject or module name
     * @param workType working mode such as remote, hybrid, or on-site
     * @param department owning department
     * @param description post description
     * @param requirements skill and eligibility requirements
     * @param openPositions maximum number of hires
     * @param deadline application deadline
     * @param hoursPerWeek expected workload hours per week
     * @param compensation compensation text
     * @param open whether the post is open for applications
     * @param moId module organizer identifier
     */
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

    /**
     * Changes whether the post is open for applications.
     *
     * @param open true to mark the post open; false to close it
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getMoId() {
        return moId;
    }

    /**
     * Returns a human-readable status label for JSP display.
     *
     * @return Open or Closed
     */
    public String getDisplayStatus() {

        return open ? "Open" : "Closed";
    }
}

