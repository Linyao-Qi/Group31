package com.tajobsystem.ui;

import com.tajobsystem.model.Job;

import javax.swing.*;
import java.awt.*;

public class JobDetailsFrame extends JFrame {

    public JobDetailsFrame(Job job) {
        setTitle("Job Details");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(job.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        infoPanel.add(new JLabel("Subject:"));
        infoPanel.add(new JLabel(job.getSubject()));

        infoPanel.add(new JLabel("Work Type:"));
        infoPanel.add(new JLabel(job.getWorkType()));

        infoPanel.add(new JLabel("Department:"));
        infoPanel.add(new JLabel(job.getDepartment()));

        infoPanel.add(new JLabel("Open Positions:"));
        infoPanel.add(new JLabel(String.valueOf(job.getOpenPositions())));

        infoPanel.add(new JLabel("Deadline:"));
        infoPanel.add(new JLabel(job.getDeadline()));

        infoPanel.add(new JLabel("Hours per Week:"));
        infoPanel.add(new JLabel(job.getHoursPerWeek()));

        infoPanel.add(new JLabel("Compensation:"));
        infoPanel.add(new JLabel(job.getCompensation()));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(infoPanel, BorderLayout.NORTH);

        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Arial", Font.PLAIN, 14));

        detailArea.setText(
                "Description:\n" + job.getDescription() + "\n\n" +
                        "Requirements:\n" + job.getRequirements()
        );

        JScrollPane detailScrollPane = new JScrollPane(detailArea);
        centerPanel.add(detailScrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}