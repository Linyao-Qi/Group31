package com.tajobsystem.ui;

import com.tajobsystem.data.JobDataLoader;
import com.tajobsystem.model.Job;
import com.tajobsystem.model.TAProfile; // 引入成员3写的TAProfile
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

    // 新增：保存当前登录的 TA 的档案
    private TAProfile currentTA;

    // 修改构造函数，把 TAProfile 传进来
    // 这是你的 TAJobsFrame 构造函数
    public TAJobsFrame(TAProfile currentTA) {
        this.currentTA = currentTA;

        setTitle("TA Jobs - Logged in as: " + (currentTA != null ? currentTA.getName() : "Guest"));
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        jobService = new JobService();

        // --- 按照你们团队的“蓝图”修改这里 ---

        // 1. 首先，加载静态的 jobs.csv 文件
        allJobs = JobDataLoader.loadJobsFromCSV("data/jobs.csv");

        // 2. 然后，加载成员4动态发布的 job.dat 文件
        List<Job> publishedJobs = JobDataLoader.loadJobsFromDat("data/job.dat");

        // 3. 把动态发布的工作合并到总列表里
        if (publishedJobs != null) {
            allJobs.addAll(publishedJobs);
        }
        // ---------------------------------

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
        sortComboBox = new JComboBox<>(new String[]{
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

        // --- 新增：底部按钮面板 ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton viewDetailsButton = new JButton("View Details");
        JButton applyButton = new JButton("Apply for Job"); // 新增申请按钮

        bottomPanel.add(viewDetailsButton);
        bottomPanel.add(applyButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- 事件监听 ---
        searchButton.addActionListener(e -> applyFilters());

        clearButton.addActionListener(e -> {
            searchField.setText("");
            subjectComboBox.setSelectedIndex(0);
            workTypeComboBox.setSelectedIndex(0);
            sortComboBox.setSelectedIndex(0);
            refreshJobList(allJobs);
        });

        viewDetailsButton.addActionListener(e -> openJobDetails());

        // 新增申请按钮的点击事件
        applyButton.addActionListener(e -> openApplyWindow());
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

        // 建议也把 TAProfile 传给详情页，这样在详情页里也可以直接申请
        new JobDetailsFrame(selectedJob, currentTA);
    }

    // --- 新增：打开成员2写的申请界面的方法 ---
    private void openApplyWindow() {
        Job selectedJob = jobJList.getSelectedValue();

        if (selectedJob == null) {
            JOptionPane.showMessageDialog(this, "Please select a job to apply for.");
            return;
        }

        if (!selectedJob.isOpen()) {
            JOptionPane.showMessageDialog(this, "This job is currently closed for applications.");
            return;
        }

        if (currentTA == null) {
            JOptionPane.showMessageDialog(this, "Error: TA Profile is missing. Please log in first.");
            return;
        }

        // 调用成员2写的 ApplyFrame
        ApplyFrame applyFrame = new ApplyFrame(this, currentTA, selectedJob);
        applyFrame.setVisible(true);
    }
}