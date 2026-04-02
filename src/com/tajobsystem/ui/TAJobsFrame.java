package com.tajobsystem.ui;

import com.tajobsystem.data.JobDataLoader;
import com.tajobsystem.model.Job;
import com.tajobsystem.service.JobService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TAJobsFrame extends JFrame {

    private JTextField searchField;
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> workTypeComboBox;
    private JComboBox<String> sortComboBox;
    private DefaultListModel<Job> listModel;
    private JList<Job> jobJList;

    private List<Job> allJobs;
    private JobService jobService;

    public TAJobsFrame() {
        setTitle("TA Jobs");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        jobService = new JobService();
        allJobs = JobDataLoader.loadJobsFromCSV("data/jobs.csv");

        initUI();
        refreshJobList(allJobs);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(6, 2, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));

        filterPanel.add(new JLabel("Search:"));
        searchField = new JTextField();
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Subject:"));
        subjectComboBox = new JComboBox<>(new String[]{
                "All Subjects", "CS101", "CS201", "CS301", "MATH301", "PHYS101", "ECON201"
        });
        filterPanel.add(subjectComboBox);

        filterPanel.add(new JLabel("Work Type:"));
        workTypeComboBox = new JComboBox<>(new String[]{
                "All Types", "Lab", "Tutorial", "Grading"
        });
        filterPanel.add(workTypeComboBox);

        filterPanel.add(new JLabel("Sort By:"));
        sortComboBox = new JComboBox<String>(new String[]{
                "Deadline", "Title"
        });
        filterPanel.add(sortComboBox);

        JButton searchButton = new JButton("Search / Filter");
        JButton clearButton = new JButton("Clear Filters");

        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        add(filterPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        jobJList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(jobJList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Positions"));

        add(scrollPane, BorderLayout.CENTER);

        JButton viewDetailsButton = new JButton("View Details");
        add(viewDetailsButton, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> applyFilters());

        clearButton.addActionListener(e -> {
            searchField.setText("");
            subjectComboBox.setSelectedIndex(0);
            workTypeComboBox.setSelectedIndex(0);
            sortComboBox.setSelectedIndex(0);
            refreshJobList(allJobs);
        });

        viewDetailsButton.addActionListener(e -> openJobDetails());
    }
    private void applyFilters() {
        String keyword = searchField.getText().trim();
        String subject = (String) subjectComboBox.getSelectedItem();
        String workType = (String) workTypeComboBox.getSelectedItem();
        String sortBy = (String) sortComboBox.getSelectedItem();

        List<Job> filteredJobs = jobService.filterJobs(allJobs, keyword, subject, workType);
        jobService.sortJobs(filteredJobs, sortBy);
        refreshJobList(filteredJobs);

        if (filteredJobs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No jobs found.");
        }
    }

    private void refreshJobList(List<Job> jobs) {
        listModel.clear();

        for (Job job : jobs) {
            listModel.addElement(job);
        }
    }

    private void openJobDetails() {
        Job selectedJob = jobJList.getSelectedValue();

        if (selectedJob == null) {
            JOptionPane.showMessageDialog(this, "Please select a job first.");
            return;
        }

        new JobDetailsFrame(selectedJob);
    }
}