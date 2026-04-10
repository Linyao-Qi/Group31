package Admin;

import java.io.IOException;
import java.util.*;


public class AdminService {
    private static final double OVERLOAD_THRESHOLD = 15.0;
    private static final String STATUS_NORMAL = "Normal";
    private static final String STATUS_OVERLOADED = "Overloaded";
    private static final String STATUS_REASSIGNING = "Reassigning";

    private final AdminDataManagement dataManagement;

    public AdminService() {
        this.dataManagement = new AdminDataManagement();
    }


    public List<AdminWorkload> getAllWorkloads() {
        try {
            List<AdminWorkload> workloads = dataManagement.loadWorkloads();
            normalizeTotalsAndStatuses(workloads, true);
            return workloads;
        } catch (IOException | NumberFormatException e) {
            return fallbackWorkloads();
        }
    }


    public boolean saveAllWorkloads(List<AdminWorkload> workloads) {
        normalizeTotalsAndStatuses(workloads, false);
        try {
            dataManagement.saveWorkloads(workloads);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public void recalculateTotalsAndStatusesInMemory(List<AdminWorkload> workloads) {
        normalizeTotalsAndStatuses(workloads, false);
    }


    public int getTotalActiveTAs(List<AdminWorkload> workloads) {
        Set<String> taIds = new HashSet<>();
        for (AdminWorkload workload : workloads) {
            taIds.add(workload.getTaId());
        }
        return taIds.size();
    }


    public int getOverloadedCount(List<AdminWorkload> workloads) {
        Set<String> overloadedTaIds = new HashSet<>();
        for (AdminWorkload workload : workloads) {
            if (isOverloaded(workload)) {
                overloadedTaIds.add(workload.getTaId());
            }
        }
        return overloadedTaIds.size();
    }

    public int getTotalAssignedModules(List<AdminWorkload> workloads) {
        return workloads.size();
    }

    public boolean isOverloaded(AdminWorkload workload) {
        return workload.getTaTotalWorkHour() > OVERLOAD_THRESHOLD;
    }


    public List<AdminRecruitment> getAllPosts() {
        try {
            return dataManagement.loadPosts();
        } catch (IOException | NumberFormatException e) {
            return fallbackPosts();
        }
    }


    public boolean saveAllPosts(List<AdminRecruitment> latestPosts) {
        try {
            dataManagement.savePosts(deepCopyPosts(latestPosts));
            return true;
        } catch (IOException e) {
            return false;
        }
    }


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
            boolean reassigning = STATUS_REASSIGNING.equalsIgnoreCase(valueOrEmpty(workload.getStatus()));
            if (reassigning) {
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

            boolean reassigning = STATUS_REASSIGNING.equalsIgnoreCase(valueOrEmpty(workload.getStatus()));
            if (!reassigning) {
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

