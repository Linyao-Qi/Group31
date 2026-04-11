package Admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class AdminWorkloadDomainService {

    public List<AdminWorkload> filterWorkloads(List<AdminWorkload> workloads, String moduleCode, String status, String moId) {
        boolean byModule = !isBlank(moduleCode);
        boolean byStatus = !isBlank(status);
        boolean byMoId = !isBlank(moId);

        List<AdminWorkload> filtered = new ArrayList<>();
        for (AdminWorkload workload : workloads) {
            boolean moduleMatch = !byModule || moduleCode.equals(valueOrEmpty(workload.getModuleCode()));
            boolean statusMatch = !byStatus || status.equals(valueOrEmpty(workload.getStatus()));
            boolean moMatch = !byMoId || moId.equals(valueOrEmpty(workload.getMoId()));
            if (moduleMatch && statusMatch && moMatch) {
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


    public Set<String> collectMoIds(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getMoId()));
        }
        return values;
    }


    public boolean markReassigning(List<AdminWorkload> workloads, String taId, String moduleCode, String moId) {
        for (AdminWorkload workload : workloads) {
            if (!valueOrEmpty(workload.getTaId()).equals(valueOrEmpty(taId))) {
                continue;
            }
            if (!valueOrEmpty(workload.getModuleCode()).equals(valueOrEmpty(moduleCode))) {
                continue;
            }
            if (!valueOrEmpty(workload.getMoId()).equals(valueOrEmpty(moId))) {
                continue;
            }
            if (!"Overloaded".equalsIgnoreCase(valueOrEmpty(workload.getStatus()))) {
                return false;
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

