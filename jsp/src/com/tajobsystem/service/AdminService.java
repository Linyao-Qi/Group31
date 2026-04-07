package com.tajobsystem.service;

import com.tajobsystem.data.AdminDataManagement;
import com.tajobsystem.model.AdminRecruitment;
import com.tajobsystem.model.AdminWorkload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminService {
    private static final double OVERLOAD_THRESHOLD = 15.0;
    private static final String STATUS_NORMAL = "Normal";
    private static final String STATUS_OVERLOADED = "Overloaded";
    private static final String STATUS_REASSIGNING = "Reassigning";

    private final List<AdminRecruitment> posts;
    private final AdminDataManagement csvRepository;

    public AdminService() {
        this.posts = new ArrayList<>();
        this.csvRepository = new AdminDataManagement();
        loadFromCsv();
    }

    public List<AdminRecruitment> getAllPosts() {
        loadFromCsv();
        return deepCopy(posts);
    }

    public boolean saveAllPosts(List<AdminRecruitment> latestPosts) {
        posts.clear();
        posts.addAll(deepCopy(latestPosts));
        return persistToCsv();
    }

    public boolean closePostChannel(String jobId) {
        return setPostOpenState(jobId, false);
    }

    public boolean openPostChannel(String jobId) {
        return setPostOpenState(jobId, true);
    }

    public int countOpenPosts() {
        return countOpenPosts(posts);
    }

    public int countOpenPosts(List<AdminRecruitment> targetPosts) {
        int count = 0;
        for (AdminRecruitment post : targetPosts) {
            if (post.isOpen()) {
                count++;
            }
        }
        return count;
    }

    public List<AdminWorkload> getAllWorkloads() {
        try {
            List<AdminWorkload> workloads = csvRepository.loadWorkloads();
            normalizeTotalsAndStatuses(workloads, true);
            return workloads;
        } catch (IOException | NumberFormatException e) {
            return fallbackWorkloads();
        }
    }

    public boolean saveAllWorkloads(List<AdminWorkload> workloads) {
        normalizeTotalsAndStatuses(workloads, false);
        try {
            csvRepository.saveWorkloads(workloads);
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

    private void loadFromCsv() {
        try {
            csvRepository.ensureCsvExists();
            posts.clear();
            posts.addAll(csvRepository.loadPosts());
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Failed to load admin post data from CSV.", e);
        }
    }

    private boolean persistToCsv() {
        try {
            csvRepository.savePosts(posts);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean setPostOpenState(String jobId, boolean targetState) {
        for (AdminRecruitment post : posts) {
            if (!post.getJobId().equals(jobId)) {
                continue;
            }
            if (post.isOpen() == targetState) {
                return false;
            }
            post.setOpen(targetState);
            if (!persistToCsv()) {
                post.setOpen(!targetState);
                return false;
            }
            return true;
        }
        return false;
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
            csvRepository.saveWorkloads(workloads);
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

    private List<AdminRecruitment> deepCopy(List<AdminRecruitment> source) {
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
}
