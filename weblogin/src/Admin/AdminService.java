package Admin;

import java.io.IOException;
import java.util.*;


/**
 * Coordinates administrator workload and recruitment operations.
 *
 * @author Yutong Yao
 * @version 1.0
 */
public class AdminService {
    private static final double OVERLOAD_THRESHOLD = 15.0;
    private static final String STATUS_NORMAL = "Normal";
    private static final String STATUS_OVERLOADED = "Overloaded";
    private static final String STATUS_CANCEL = "Cancel";

    private final AdminDataManagement dataManagement;
    private final AdminWorkloadApplicationSyncService workloadApplicationSyncService;

    /**
     * Creates the service with CSV-backed data management.
     */
    public AdminService() {
        this.dataManagement = new AdminDataManagement();
        this.workloadApplicationSyncService = new AdminWorkloadApplicationSyncService(this.dataManagement);
    }


    /**
     * Loads all workloads, merges accepted applications, and normalizes totals and statuses.
     *
     * @return current workload list, or fallback data if loading fails
     */
    public List<AdminWorkload> getAllWorkloads() {
        try {
            List<AdminWorkload> workloads = dataManagement.loadWorkloads();
            workloadApplicationSyncService.appendAcceptedApplicationsToWorkloads(workloads);
            normalizeTotalsAndStatuses(workloads, true);
            return workloads;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return fallbackWorkloads();
        }
    }


    /**
     * Saves workload changes and propagates cancelled workloads back to applications.
     *
     * @param workloads workloads to persist
     * @return true when the save succeeds; false otherwise
     */
    public boolean saveAllWorkloads(List<AdminWorkload> workloads) {
        normalizeTotalsAndStatuses(workloads, false);
        try {
            dataManagement.saveWorkloads(workloads);
            syncCancelledWorkloadsToApplications(workloads);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Recalculates workload totals and overload statuses without saving them.
     *
     * @param workloads workloads to update in memory
     */
    public void recalculateTotalsAndStatusesInMemory(List<AdminWorkload> workloads) {
        normalizeTotalsAndStatuses(workloads, false);
    }

    /**
     * Marks the matching application as cancelled after an admin cancels a workload.
     *
     * @param taId teaching assistant identifier
     * @param moduleCode module or job code
     * @param moId module organizer identifier
     */
    public void rejectApplicationForCancelledWorkload(String taId, String moduleCode, String moId) {
        workloadApplicationSyncService.rejectApplicationForCancelledWorkload(taId, moduleCode, moId);
    }

    /**
     * Synchronizes every cancelled workload in the list to the matching application status.
     *
     * @param workloads workloads to inspect
     */
    public void syncCancelledWorkloadsToApplications(List<AdminWorkload> workloads) {
        if (workloads == null || workloads.isEmpty()) {
            return;
        }
        for (AdminWorkload workload : workloads) {
            if (!STATUS_CANCEL.equalsIgnoreCase(valueOrEmpty(workload.getStatus()))) {
                continue;
            }
            workloadApplicationSyncService.rejectApplicationForCancelledWorkload(
                    workload.getTaId(),
                    workload.getModuleCode(),
                    workload.getMoId()
            );
        }
    }


    /**
     * Counts distinct teaching assistants represented in the workload list.
     *
     * @param workloads workloads to inspect
     * @return number of distinct TA identifiers
     */
    public int getTotalActiveTAs(List<AdminWorkload> workloads) {
        Set<String> taIds = new HashSet<>();
        for (AdminWorkload workload : workloads) {
            taIds.add(workload.getTaId());
        }
        return taIds.size();
    }


    /**
     * Counts distinct TAs whose total workload exceeds the overload threshold.
     *
     * @param workloads workloads to inspect
     * @return number of overloaded TAs
     */
    public int getOverloadedCount(List<AdminWorkload> workloads) {
        Set<String> overloadedTaIds = new HashSet<>();
        for (AdminWorkload workload : workloads) {
            if (isOverloaded(workload)) {
                overloadedTaIds.add(workload.getTaId());
            }
        }
        return overloadedTaIds.size();
    }

    /**
     * Counts assigned workload rows.
     *
     * @param workloads workloads to inspect
     * @return number of assigned modules
     */
    public int getTotalAssignedModules(List<AdminWorkload> workloads) {
        return workloads.size();
    }

    /**
     * Determines whether a workload row belongs to an overloaded TA.
     *
     * @param workload workload to check
     * @return true when total TA hours are above the threshold
     */
    public boolean isOverloaded(AdminWorkload workload) {
        return workload.getTaTotalWorkHour() > OVERLOAD_THRESHOLD;
    }


    /**
     * Loads all recruitment posts.
     *
     * @return recruitment posts, or fallback data if loading fails
     */
    public List<AdminRecruitment> getAllPosts() {
        try {
            return dataManagement.loadPosts();
        } catch (IOException | NumberFormatException e) {
            return fallbackPosts();
        }
    }


    /**
     * Saves recruitment posts after copying the supplied list.
     *
     * @param latestPosts posts to persist
     * @return true when the save succeeds; false otherwise
     */
    public boolean saveAllPosts(List<AdminRecruitment> latestPosts) {
        try {
            dataManagement.savePosts(deepCopyPosts(latestPosts));
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Counts posts that are currently open.
     *
     * @param posts posts to inspect
     * @return number of open posts
     */
    public int countOpenPosts(List<AdminRecruitment> posts) {
        int count = 0;
        for (AdminRecruitment post : posts) {
            if (post.isOpen()) {
                count++;
            }
        }
        return count;
    }


    private void normalizeTotalsAndStatuses(List<AdminWorkload> workloads, boolean persistWhenChanged) {
        Map<String, Double> taTotalHourMap = new HashMap<>();

        for (AdminWorkload workload : workloads) {
            String taKey = buildTaKey(workload);
            boolean cancelled = STATUS_CANCEL.equalsIgnoreCase(valueOrEmpty(workload.getStatus()));
            if (cancelled) {
                continue;
            }
            double newTotal = taTotalHourMap.getOrDefault(taKey, 0.0) + workload.getCourseWorkHour();
            taTotalHourMap.put(taKey, newTotal);
        }

        boolean changed = false;
        for (AdminWorkload workload : workloads) {
            String taKey = buildTaKey(workload);
            double computedTotal = taTotalHourMap.getOrDefault(taKey, 0.0);

            if (Math.abs(workload.getTaTotalWorkHour() - computedTotal) > 1e-9) {
                workload.setTaTotalWorkHour(computedTotal);
                changed = true;
            }

            boolean cancelled = STATUS_CANCEL.equalsIgnoreCase(valueOrEmpty(workload.getStatus()));
            if (!cancelled) {
                String computedStatus = computedTotal > OVERLOAD_THRESHOLD ? STATUS_OVERLOADED : STATUS_NORMAL;
                if (!computedStatus.equals(workload.getStatus())) {
                    workload.setStatus(computedStatus);
                    changed = true;
                }
            }
        }

        if (!changed || !persistWhenChanged) {
            return;
        }
        try {
            dataManagement.saveWorkloads(workloads);
        } catch (IOException ignored) {
        }
    }

    private String buildTaKey(AdminWorkload workload) {
        String taId = valueOrEmpty(workload.getTaId());
        if (!taId.isEmpty()) {
            return taId;
        }
        return valueOrEmpty(workload.getTaName());
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private List<AdminRecruitment> deepCopyPosts(List<AdminRecruitment> source) {
        List<AdminRecruitment> copied = new ArrayList<>();
        for (AdminRecruitment post : source) {
            copied.add(new AdminRecruitment(
                    post.getJobId(),
                    post.getTitle(),
                    post.getSubject(),
                    post.getWorkType(),
                    post.getDepartment(),
                    post.getDescription(),
                    post.getRequirements(),
                    post.getOpenPositions(),
                    post.getDeadline(),
                    post.getHoursPerWeek(),
                    post.getCompensation(),
                    post.isOpen(),
                    post.getMoId()
            ));
        }
        return copied;
    }

    private List<AdminWorkload> fallbackWorkloads() {
        List<AdminWorkload> workloads = new ArrayList<>();
        workloads.add(new AdminWorkload("Amy", "MO001", "TA001", "Alice", "Data Structures", "CS101", 6, 20));
        workloads.add(new AdminWorkload("Amy", "MO001", "TA001", "Alice", "Programming Basics", "CS100", 8, 20));
        workloads.add(new AdminWorkload("Amy", "MO001", "TA001", "Alice", "Programming Basics", "CS100", 6, 20));
        return workloads;
    }

    private List<AdminRecruitment> fallbackPosts() {
        List<AdminRecruitment> posts = new ArrayList<>();
        posts.add(new AdminRecruitment(
                "J001", "TA - Lab Support", "CS101", "On-site", "Computer Science",
                "Support weekly lab sessions", "Java and basic debugging", 3,
                "2026-05-01", 8, "18/hour", true, "MO001"
        ));
        return posts;
    }
}

