package Admin;


public class AdminWorkload {
    private String moName;
    private String moId;
    private String taId;
    private String taName;
    private String moduleName;
    private String moduleCode;
    private double courseWorkHour;
    private double taTotalWorkHour;
    private String status;

    public AdminWorkload(
            String moName,
            String moId,
            String taId,
            String taName,
            String moduleName,
            String moduleCode,
            double courseWorkHour,
            double taTotalWorkHour
    ) {
        this.moName = moName;
        this.moId = moId;
        this.taId = taId;
        this.taName = taName;
        this.moduleName = moduleName;
        this.moduleCode = moduleCode;
        this.courseWorkHour = courseWorkHour;
        this.taTotalWorkHour = taTotalWorkHour;
        this.status = "Normal";
    }

    public String getMoName() {
        return moName;
    }

    public void setMoName(String moName) {
        this.moName = moName;
    }

    public String getMoId() {
        return moId;
    }

    public void setMoId(String moId) {
        this.moId = moId;
    }

    public String getTaId() {
        return taId;
    }

    public void setTaId(String taId) {
        this.taId = taId;
    }

    public String getTaName() {
        return taName;
    }

    public void setTaName(String taName) {
        this.taName = taName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public double getCourseWorkHour() {
        return courseWorkHour;
    }

    public void setCourseWorkHour(double courseWorkHour) {
        this.courseWorkHour = courseWorkHour;
    }

    public double getTaTotalWorkHour() {
        return taTotalWorkHour;
    }

    public void setTaTotalWorkHour(double taTotalWorkHour) {
        this.taTotalWorkHour = taTotalWorkHour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

