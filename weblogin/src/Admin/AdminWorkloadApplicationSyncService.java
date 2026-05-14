package Admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminWorkloadApplicationSyncService {
    private final AdminDataManagement dataManagement;

    public AdminWorkloadApplicationSyncService(AdminDataManagement dataManagement) {
        this.dataManagement = dataManagement;
    }

    public boolean appendAcceptedApplicationsToWorkloads(List<AdminWorkload> workloads) {
        try {
            List<AdminApplicationRecord> applications = dataManagement.loadApplications();
            List<AdminRecruitment> jobs = dataManagement.loadPosts();
            Map<String, AdminRecruitment> jobsById = buildJobsById(jobs);
            Map<String, String> moNameByMoId = buildMoNameByMoId(workloads);

            boolean changed = false;
            for (AdminApplicationRecord application : applications) {
                String appStatus = normalizeStatus(application.getAppStatus());
                if (!"ACCEPTED".equals(appStatus) && !"CANCELED".equals(appStatus)) {
                    continue;
                }

                String jobId = safe(application.getJobId());
                AdminRecruitment job = jobsById.get(jobId);
                if (job == null) {
                    continue; // Incomplete data: skip safely.
                }

                String taId = safe(application.getTaId());
                String moId = firstNonBlank(safe(application.getMoId()), safe(job.getMoId()));
                if (taId.isEmpty() || jobId.isEmpty()) {
                    continue;
                }
                String mergedWorkloadStatus = "CANCELED".equals(appStatus) ? "Cancel" : "Normal";
                AdminWorkload existing = findWorkload(workloads, taId, moId, jobId);
                if (existing != null) {
                    if ("Cancel".equals(mergedWorkloadStatus) && !"Cancel".equalsIgnoreCase(safe(existing.getStatus()))) {
                        existing.setStatus("Cancel");
                        changed = true;
                    }
                    continue;
                }

                String taName = firstNonBlank(safe(application.getName()), taId);
                String moduleName = firstNonBlank(safe(job.getSubject()), jobId);
                String moName = resolveMoName(moNameByMoId, moId);
                double courseWorkHour = job.getHoursPerWeek();

                AdminWorkload newWorkload = new AdminWorkload(
                        moName,
                        moId,
                        taId,
                        taName,
                        moduleName,
                        jobId,
                        courseWorkHour,
                        0.0
                );
                newWorkload.setStatus(mergedWorkloadStatus);
                workloads.add(newWorkload);
                changed = true;
            }
            return changed;
        } catch (IOException ignored) {
            return false;
        }
    }

    public void rejectApplicationForCancelledWorkload(String taId, String moduleCode, String moId) {
        try {
            List<AdminApplicationRecord> applications = dataManagement.loadApplications();
            boolean changed = false;

            for (AdminApplicationRecord application : applications) {
                if (!safe(application.getTaId()).equals(safe(taId))) {
                    continue;
                }
                if (!safe(application.getJobId()).equals(safe(moduleCode))) {
                    continue;
                }
                if (!safe(moId).isEmpty() && !safe(application.getMoId()).equals(safe(moId))) {
                    continue;
                }
                application.setAppStatus("CANCELED");
                changed = true;
                break;
            }

            if (changed) {
                dataManagement.saveApplications(applications);
            }
        } catch (IOException ignored) {
            // Data not enough or file issue: keep silent and avoid runtime errors.
        }
    }

    private Map<String, AdminRecruitment> buildJobsById(List<AdminRecruitment> jobs) {
        Map<String, AdminRecruitment> jobsById = new HashMap<>();
        for (AdminRecruitment job : jobs) {
            String jobId = safe(job.getJobId());
            if (!jobId.isEmpty()) {
                jobsById.put(jobId, job);
            }
        }
        return jobsById;
    }

    private Map<String, String> buildMoNameByMoId(List<AdminWorkload> workloads) {
        Map<String, String> map = new HashMap<>();
        for (AdminWorkload workload : workloads) {
            String moId = safe(workload.getMoId());
            String moName = safe(workload.getMoName());
            if (!moId.isEmpty() && !moName.isEmpty() && !map.containsKey(moId)) {
                map.put(moId, moName);
            }
        }
        return map;
    }

    private String resolveMoName(Map<String, String> moNameByMoId, String moId) {
        String found = moNameByMoId.get(safe(moId));
        if (!safe(found).isEmpty()) {
            return found;
        }
        return safe(moId).toUpperCase();
    }

    private AdminWorkload findWorkload(List<AdminWorkload> workloads, String taId, String moId, String moduleCode) {
        for (AdminWorkload workload : workloads) {
            if (!safe(workload.getTaId()).equals(safe(taId))) {
                continue;
            }
            if (!safe(workload.getModuleCode()).equals(safe(moduleCode))) {
                continue;
            }
            if (!safe(moId).isEmpty() && !safe(workload.getMoId()).equals(safe(moId))) {
                continue;
            }
            return workload;
        }
        return null;
    }

    private String normalizeStatus(String value) {
        String status = safe(value).toUpperCase();
        if ("CANCELLED".equals(status)) {
            return "CANCELED";
        }
        return status;
    }

    private String firstNonBlank(String first, String second) {
        return !safe(first).isEmpty() ? safe(first) : safe(second);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
