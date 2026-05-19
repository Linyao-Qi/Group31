package Admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class AdminWorkloadDomainService {

    public List<AdminWorkload> filterWorkloads(List<AdminWorkload> workloads, String moduleCode, String status, String moId) {
        return filterAndSortWorkloads(workloads, moduleCode, status, moId, "", "", "");
    }

    public List<AdminWorkload> filterAndSortWorkloads(
            List<AdminWorkload> workloads,
            String moduleCode,
            String status,
            String moId,
            String taId,
            String sortBy,
            String sortOrder
    ) {
        boolean byModule = !isBlank(moduleCode);
        boolean byStatus = !isBlank(status);
        boolean byMoId = !isBlank(moId);
        boolean byTaId = !isBlank(taId);

        List<AdminWorkload> filtered = new ArrayList<>();
        for (AdminWorkload workload : workloads) {
            boolean moduleMatch = !byModule || moduleCode.equals(valueOrEmpty(workload.getModuleCode()));
            boolean statusMatch = !byStatus || status.equals(valueOrEmpty(workload.getStatus()));
            boolean moMatch = !byMoId || moId.equals(valueOrEmpty(workload.getMoId()));
            boolean taMatch = !byTaId || taId.equals(valueOrEmpty(workload.getTaId()));
            if (moduleMatch && statusMatch && moMatch && taMatch) {
                filtered.add(workload);
            }
        }
        sortWorkloads(filtered, sortBy, sortOrder);
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


    public Set<String> collectTaIds(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getTaId()));
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
            workload.setStatus("Cancel");
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

    private void sortWorkloads(List<AdminWorkload> workloads, String sortBy, String sortOrder) {
        Comparator<AdminWorkload> comparator = null;
        if ("moId".equals(sortBy)) {
            comparator = Comparator.comparing((AdminWorkload workload) -> valueOrEmpty(workload.getMoId()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing((AdminWorkload workload) -> valueOrEmpty(workload.getTaId()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing((AdminWorkload workload) -> valueOrEmpty(workload.getModuleCode()), String.CASE_INSENSITIVE_ORDER);
        } else if ("taId".equals(sortBy)) {
            comparator = Comparator.comparing((AdminWorkload workload) -> valueOrEmpty(workload.getTaId()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing((AdminWorkload workload) -> valueOrEmpty(workload.getMoId()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing((AdminWorkload workload) -> valueOrEmpty(workload.getModuleCode()), String.CASE_INSENSITIVE_ORDER);
        }

        if (comparator == null) {
            return;
        }
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        workloads.sort(comparator);
    }
}

