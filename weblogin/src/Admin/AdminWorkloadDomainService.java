package Admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Provides filtering, sorting, and status-changing rules for workload drafts.
 *
 * @author Yutong Yao
 * @version 1.0
 */
public class AdminWorkloadDomainService {

    /**
     * Filters workloads by module code, status, and module organizer.
     *
     * @param workloads source workloads
     * @param moduleCode optional module code filter
     * @param status optional status filter
     * @param moId optional module organizer filter
     * @return filtered workloads
     */
    public List<AdminWorkload> filterWorkloads(List<AdminWorkload> workloads, String moduleCode, String status, String moId) {
        return filterAndSortWorkloads(workloads, moduleCode, status, moId, "", "", "");
    }

    /**
     * Filters workloads and applies an optional stable sort for admin tables.
     *
     * @param workloads source workloads
     * @param moduleCode optional module code filter
     * @param status optional workload status filter
     * @param moId optional module organizer filter
     * @param taId optional TA identifier filter
     * @param sortBy sort key, currently moId or taId
     * @param sortOrder asc or desc
     * @return filtered and sorted workloads
     */
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


    /**
     * Collects module codes for filter controls.
     *
     * @param workloads workloads to inspect
     * @return sorted unique module codes
     */
    public Set<String> collectModuleCodes(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getModuleCode()));
        }
        return values;
    }


    /**
     * Collects workload statuses for filter controls.
     *
     * @param workloads workloads to inspect
     * @return sorted unique statuses
     */
    public Set<String> collectStatuses(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getStatus()));
        }
        return values;
    }


    /**
     * Collects module organizer identifiers for filter controls.
     *
     * @param workloads workloads to inspect
     * @return sorted unique MO identifiers
     */
    public Set<String> collectMoIds(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getMoId()));
        }
        return values;
    }


    /**
     * Collects teaching assistant identifiers for filter controls.
     *
     * @param workloads workloads to inspect
     * @return sorted unique TA identifiers
     */
    public Set<String> collectTaIds(List<AdminWorkload> workloads) {
        Set<String> values = new TreeSet<>();
        for (AdminWorkload workload : workloads) {
            values.add(valueOrEmpty(workload.getTaId()));
        }
        return values;
    }


    /**
     * Marks an overloaded workload as cancelled so it can be reassigned.
     *
     * @param workloads workload draft list
     * @param taId target TA identifier
     * @param moduleCode target module code
     * @param moId target module organizer identifier
     * @return true when a matching overloaded workload was marked as Cancel
     */
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

