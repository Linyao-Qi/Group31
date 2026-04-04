package com.tajobsystem.ui;

import com.tajobsystem.model.Job;
import com.tajobsystem.model.TAProfile;
import com.tajobsystem.service.ApplicationService;
import com.tajobsystem.service.ApplicationService.ApplyResult;
import com.tajobsystem.service.ApplicationService.WithdrawResult;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for a TA to apply for a specific job.
 * The TA selects one of their previously uploaded CV PDFs from data/cvs/{taId}/.
 */
public class ApplyFrame extends JDialog {

    private static final String CV_BASE_DIR = "./data/cvs/";

    private final TAProfile taProfile;
    private final Job job;
    private final ApplicationService service = new ApplicationService();

    private JComboBox<String> cvComboBox;   // displays CV file names
    private List<File> availableCVs;        // parallel list of File objects

    public ApplyFrame(Frame parent, TAProfile taProfile, Job job) {
        super(parent, "Apply for Position", true);
        this.taProfile = taProfile;
        this.job = job;
        this.availableCVs = loadCVsForTA(taProfile.getTaId());
        initUI();
        pack();
        setMinimumSize(new Dimension(480, 0));
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // ---- Job summary ----
        JPanel jobPanel = new JPanel(new GridLayout(0, 2, 6, 4));
        jobPanel.setBorder(BorderFactory.createTitledBorder("Position"));
        addRow(jobPanel, "Title:",        job.getTitle());
        addRow(jobPanel, "Subject:",      job.getSubject());
        addRow(jobPanel, "Work Type:",    job.getWorkType());
        addRow(jobPanel, "Deadline:",     job.getDeadline());
        addRow(jobPanel, "Hours/Week:",   job.getHoursPerWeek());
        addRow(jobPanel, "Compensation:", job.getCompensation());
        add(jobPanel, BorderLayout.NORTH);

        // ---- TA profile + CV selection ----
        JPanel taPanel = new JPanel(new GridLayout(0, 2, 6, 4));
        taPanel.setBorder(BorderFactory.createTitledBorder("Your Profile"));
        addRow(taPanel, "Name:",   taProfile.getName());
        addRow(taPanel, "Email:",  taProfile.getEmail());
        addRow(taPanel, "Skills:", taProfile.getSkills());

        taPanel.add(new JLabel("Select CV:"));
        if (availableCVs.isEmpty()) {
            taPanel.add(new JLabel("<html><i style='color:gray'>No CVs found in data/cvs/"
                    + taProfile.getTaId() + "/</i></html>"));
        } else {
            String[] cvNames = availableCVs.stream()
                    .map(File::getName)
                    .toArray(String[]::new);
            cvComboBox = new JComboBox<>(cvNames);
            taPanel.add(cvComboBox);
        }
        add(taPanel, BorderLayout.CENTER);

        // ---- Buttons ----
        JButton confirmBtn = new JButton("Submit");
        JButton cancelBtn  = new JButton("Cancel");

        confirmBtn.addActionListener(e -> handleApply());
        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void handleApply() {
        String cvFilePath = null;
        if (cvComboBox != null) {
            int idx = cvComboBox.getSelectedIndex();
            if (idx >= 0) {
                cvFilePath = availableCVs.get(idx).getPath();
            }
        }

        if (cvFilePath == null) {
            Object[] noCvOptions = {"Submit Anyway", "Cancel"};
            int confirm = JOptionPane.showOptionDialog(this,
                    "No CV selected. Submit application without a CV?",
                    "No CV Selected",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, noCvOptions, noCvOptions[1]);
            if (confirm != 0) return;
        }

        ApplyResult result = service.applyForJob(taProfile, job, cvFilePath);

        if (result.isSuccess()) {
            String cvName = (cvFilePath != null) ? new File(cvFilePath).getName() : "none";
            JOptionPane.showMessageDialog(this,
                    "Application submitted successfully!\n"
                    + "Application ID: " + result.application.getAppId() + "\n"
                    + "CV submitted: " + cvName,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else if (result.status == ApplyResult.Status.DUPLICATE) {
            handleDuplicate(cvFilePath);
        } else {
            JOptionPane.showMessageDialog(this,
                    result.message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Called when the TA already has an active application for this job.
     * Offers to withdraw the previous application; the TA can then click
     * Submit again to submit a fresh application.
     */
    private void handleDuplicate(String cvFilePath) {
        Object[] options = {"Withdraw Previous Application", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "You have already applied for this position.\n"
                + "Would you like to withdraw your previous application?\n"
                + "You can then click Submit to apply again.",
                "Already Applied",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[1]);

        if (choice != 0) return;

        // Confirm before withdrawal
        Object[] confirmOptions = {"Confirm Withdrawal", "Cancel"};
        int confirm = JOptionPane.showOptionDialog(this,
                "Are you sure you want to withdraw your previous application?\n"
                + "It will remain visible in your history with status \"Withdrawn\".",
                "Confirm Withdrawal",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, confirmOptions, confirmOptions[1]);

        if (confirm != 0) return;

        WithdrawResult result = service.withdrawActiveApplication(
                taProfile.getTaId(), job.getJobId());
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    "Previous application withdrawn.\nClick Submit to apply again.",
                    "Withdrawn", JOptionPane.INFORMATION_MESSAGE);
            // Keep dialog open — user clicks Submit to reapply
        } else {
            JOptionPane.showMessageDialog(this,
                    result.message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Scans data/cvs/{taId}/ and returns all .pdf files found. */
    private static List<File> loadCVsForTA(String taId) {
        List<File> cvs = new ArrayList<>();
        File dir = new File(CV_BASE_DIR + taId);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(f -> f.isFile()
                    && f.getName().toLowerCase().endsWith(".pdf"));
            if (files != null) {
                for (File f : files) cvs.add(f);
            }
        }
        return cvs;
    }

    private static void addRow(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        panel.add(new JLabel(value != null ? value : "-"));
    }
}