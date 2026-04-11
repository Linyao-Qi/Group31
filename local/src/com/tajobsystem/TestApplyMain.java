package com.tajobsystem;

import com.tajobsystem.data.JobDataLoader;
import com.tajobsystem.data.TAProfileLoader;
import com.tajobsystem.model.Job;
import com.tajobsystem.model.TAProfile;
import com.tajobsystem.ui.ApplicationStatusFrame;
import com.tajobsystem.ui.ApplyFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Standalone test launcher for the Apply and Application Status features.
 * Loads TA profiles from data/ta_profiles.csv and jobs from data/jobs.csv.
 * Run this class directly to test without touching Main.java.
 */
public class TestApplyMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<TAProfile> profiles = TAProfileLoader.loadProfilesFromCSV("data/ta_profiles.csv");
            List<Job> jobs = JobDataLoader.loadJobsFromCSV("data/jobs.csv");

            if (profiles.isEmpty() || jobs.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Could not load data. Check data/ folder.");
                return;
            }

            // Step 1: choose TA
            String[] taOptions = profiles.stream()
                    .map(p -> p.getTaId() + " – " + p.getName())
                    .toArray(String[]::new);
            int taChoice = showGridOptionDialog(null, "Select a TA profile to test with:",
                    "Test – Select TA", taOptions);
            if (taChoice < 0) return;
            TAProfile ta = profiles.get(taChoice);

            // Step 2: choose action
            String[] actions = {"Apply for a Job", "View Application Status"};
            int action = showGridOptionDialog(null, "What would you like to do?",
                    "Test – Select Action", actions);
            if (action < 0) return;

            if (action == 0) {
                // Apply flow: choose job then open ApplyFrame
                String[] jobOptions = jobs.stream()
                        .map(j -> j.getJobId() + " – " + j.getTitle())
                        .toArray(String[]::new);
                int jobChoice = showGridOptionDialog(null, "Select a job to apply for:",
                        "Test – Select Job", jobOptions);
                if (jobChoice < 0) return;
                Job selectedJob = jobs.get(jobChoice);
                new ApplyFrame(null, ta, selectedJob).setVisible(true);

            } else {
                // Status / history view
                new ApplicationStatusFrame(null, ta, jobs).setVisible(true);
            }
        });
    }

    /**
     * Shows a dialog with option buttons arranged in a 2-column grid.
     * Returns the selected index, or -1 if cancelled.
     */
    private static int showGridOptionDialog(Frame parent, String message, String title, String[] options) {
        JDialog dialog = new JDialog(parent, title, true);
        int[] result = {-1};

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.add(new JLabel(message), BorderLayout.NORTH);

        // 2-column grid for buttons
        int cols = 2;
        int rows = (int) Math.ceil(options.length / (double) cols);
        JPanel btnGrid = new JPanel(new GridLayout(rows, cols, 8, 8));
        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            JButton btn = new JButton(options[i]);
            btn.addActionListener(e -> { result[0] = idx; dialog.dispose(); });
            btnGrid.add(btn);
        }
        // Fill empty cell if odd number of options
        if (options.length % 2 != 0) btnGrid.add(new JLabel());

        root.add(btnGrid, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }
}
