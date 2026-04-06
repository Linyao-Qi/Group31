package com.tajobsystem.service;

import com.tajobsystem.data.AdminDataManagement;
import com.tajobsystem.model.Admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    private final List<Admin> posts;
    private final AdminDataManagement csvRepository;

    public AdminService() {
        this.posts = new ArrayList<>();
        this.csvRepository = new AdminDataManagement();
        loadFromCsv();
    }

    public List<Admin> getAllPosts() {
        loadFromCsv();
        return deepCopy(posts);
    }

    public boolean saveAllPosts(List<Admin> latestPosts) {
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

    public int countOpenPosts(List<Admin> targetPosts) {
        int count = 0;
        for (Admin post : targetPosts) {
            if (post.isOpen()) {
                count++;
            }
        }
        return count;
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
        for (Admin post : posts) {
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

    private List<Admin> deepCopy(List<Admin> source) {
        List<Admin> copied = new ArrayList<>();
        for (Admin post : source) {
            copied.add(new Admin(
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
}
