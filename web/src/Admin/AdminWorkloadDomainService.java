package Admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class AdminWorkloadDomainService {

    public List<AdminWorkload> filterWorkloads(List<AdminWorkload> workloads, String moduleCode, String status) {
        boolean byModule = !isBlank(moduleCode);
        boolean byStatus = !isBlank(status);

        List<AdminWorkload> filtered = new ArrayList<>();
        for (AdminWorkload workload : workloads) {
            boolean moduleMatch = !byModule || moduleCode.equals(valueOrEmpty(workload.getModuleCode()));
            boolean statusMatch = !byStatus || status.equals(valueOrEmpty(workload.getStatus()));
            if (moduleMatch && statusMatch) {
                filtered.add(workload);
            }
        }
        return filtered;
    }


    public Set<String> collectModuleCodes(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getModuleCode()));
        }
        return values;
    }


    public Set<String> collectStatuses(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getStatus()));
        }
        return values;
    }


    public boolean markReassigning(List<AdminWorkload> workloads, String taId, String moduleCode) {
        for (AdminWorkload workload : workloads) {
            if (!valueOrEmpty(workload.getTaId()).equals(valueOrEmpty(taId))) {
                continue;
            }
            if (!valueOrEmpty(workload.getModuleCode()).equals(valueOrEmpty(moduleCode))) {
                continue;
            }
            if (!"Overloaded".equalsIgnoreCase(valueOrEmpty(workload.getStatus()))) {
                continue;
            }
            workload.setStatus("Reassigning");
            return true;
        }
        return false;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
