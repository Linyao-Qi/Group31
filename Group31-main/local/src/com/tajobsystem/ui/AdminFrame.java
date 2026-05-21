package com.tajobsystem.ui;

import com.tajobsystem.model.AdminRecruitment;
import com.tajobsystem.model.AdminWorkload;
import com.tajobsystem.service.AdminService;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AdminFrame extends JFrame {
    private static final String CARD_HOME = "home";
    private static final String CARD_WORKLOAD = "workload";
    private static final String CARD_CLOSEPOST = "closepost";

    private static final int W_TA_ID_COLUMN_INDEX = 2;
    private static final int W_MO_ID_COLUMN_INDEX = 1;
    private static final int W_MODULE_CODE_COLUMN_INDEX = 5;
    private static final int W_STATUS_COLUMN_INDEX = 8;
    private static final int W_ACTION_COLUMN_INDEX = 9;

    private static final int P_STATUS_COLUMN_INDEX = 11;
    private static final int P_ACTION_COLUMN_INDEX = 12;

    private final AdminService adminService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final JLabel totalTAsLabel;
    private final JLabel totalModulesLabel;
    private final JLabel overloadedLabel;
    private final JComboBox<String> moduleCodeFilterCombo;
    private final JComboBox<String> statusFilterCombo;
    private final JComboBox<String> moIdFilterCombo;
    private final DefaultTableModel workloadTableModel;
    private final JTable workloadTable;
    private List<AdminWorkload> allWorkloads;
    private List<AdminWorkload> currentDisplayedWorkloads;
    private boolean hasUnsavedWorkloadChanges;

    private final JLabel openPostsLabel;
    private final DefaultTableModel postTableModel;
    private final JTable postTable;
    private List<AdminRecruitment> currentPosts;
    private boolean hasUnsavedPostChanges;

    public AdminFrame() {
        this.adminService = new AdminService();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        this.allWorkloads = new ArrayList<>();
        this.currentDisplayedWorkloads = new ArrayList<>();
        this.currentPosts = new ArrayList<>();

        setTitle("Admin Console");
        setSize(1280, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel homePanel = buildHomePanel();
        JPanel workloadPanel = new JPanel(new BorderLayout(10, 10));
        JPanel postPanel = new JPanel(new BorderLayout(10, 10));

        totalTAsLabel = new JLabel("Total Active TAs: 0");
        totalModulesLabel = new JLabel("Total Assigned Modules: 0");
        overloadedLabel = new JLabel("Overloaded TAs: 0");
        moduleCodeFilterCombo = new JComboBox<>();
        statusFilterCombo = new JComboBox<>();
        moIdFilterCombo = new JComboBox<>();
        moduleCodeFilterCombo.setPreferredSize(new Dimension(170, 28));
        statusFilterCombo.setPreferredSize(new Dimension(150, 28));
        moIdFilterCombo.setPreferredSize(new Dimension(150, 28));

        String[] workloadColumns = {
                "MO Name", "MO ID", "TA ID", "TA Name", "Module Name",
                "Module Code", "Course WorkHour", "TA Total WorkHour", "Status", "Actions"
        };
        workloadTableModel = new DefaultTableModel(workloadColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == W_ACTION_COLUMN_INDEX;
            }
        };
        workloadTable = new JTable(workloadTableModel);

        openPostsLabel = new JLabel("Open Posts: 0");
        String[] postColumns = {
                "Job ID", "Title", "Subject", "Work Type", "Department", "Description",
                "Requirements", "Open Positions", "Deadline", "Hours/Week", "Compensation", "Status", "Actions"
        };
        postTableModel = new DefaultTableModel(postColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == P_ACTION_COLUMN_INDEX;
            }
        };
        postTable = new JTable(postTableModel);

        buildWorkloadPanel(workloadPanel);
        buildPostPanel(postPanel);

        cardPanel.add(homePanel, CARD_HOME);
        cardPanel.add(workloadPanel, CARD_WORKLOAD);
        cardPanel.add(postPanel, CARD_CLOSEPOST);

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, CARD_HOME);
    }

    private JPanel buildHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(120, 0, 120, 0));

        JButton checkWorkloadButton = new JButton("Open CheckWorkload");
        JButton closePostButton = new JButton("Open ClosePost");
        Dimension buttonSize = new Dimension(240, 40);
        checkWorkloadButton.setMaximumSize(buttonSize);
        checkWorkloadButton.setPreferredSize(buttonSize);
        closePostButton.setMaximumSize(buttonSize);
        closePostButton.setPreferredSize(buttonSize);
        checkWorkloadButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        closePostButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        checkWorkloadButton.addActionListener(e -> {
            refreshAllWorkloadData();
            cardLayout.show(cardPanel, CARD_WORKLOAD);
        });
        closePostButton.addActionListener(e -> {
            refreshPostData();
            cardLayout.show(cardPanel, CARD_CLOSEPOST);
        });

        panel.add(Box.createVerticalGlue());
        panel.add(checkWorkloadButton);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(closePostButton);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void buildWorkloadPanel(JPanel panel) {
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        overloadedLabel.setForeground(new Color(200, 0, 0));
        summaryPanel.add(totalTAsLabel);
        summaryPanel.add(totalModulesLabel);
        summaryPanel.add(overloadedLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        filterPanel.add(new JLabel("Module Code:"));
        filterPanel.add(moduleCodeFilterCombo);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilterCombo);
        filterPanel.add(new JLabel("MO ID:"));
        filterPanel.add(moIdFilterCombo);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(summaryPanel, BorderLayout.NORTH);
        northContainer.add(filterPanel, BorderLayout.SOUTH);

        workloadTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workloadTable.setRowHeight(24);
        workloadTable.getColumnModel().getColumn(W_STATUS_COLUMN_INDEX).setCellRenderer(new WorkloadStatusCellRenderer());
        workloadTable.getColumnModel().getColumn(W_ACTION_COLUMN_INDEX).setCellRenderer(new WorkloadActionCellRenderer());
        workloadTable.getColumnModel().getColumn(W_ACTION_COLUMN_INDEX).setCellEditor(new WorkloadActionCellEditor());
        workloadTable.getColumnModel().getColumn(W_ACTION_COLUMN_INDEX).setPreferredWidth(130);

        JScrollPane tableScrollPane = new JScrollPane(workloadTable);
        tableScrollPane.setPreferredSize(new Dimension(1200, 460));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton backButton = new JButton("Back");
        JButton resetFiltersButton = new JButton("Reset Filters");
        JButton refreshButton = new JButton("Refresh");
        JButton saveButton = new JButton("Save");
        bottomPanel.add(backButton);
        bottomPanel.add(resetFiltersButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(saveButton);

        panel.add(northContainer, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        moduleCodeFilterCombo.addActionListener(e -> applyWorkloadFiltersAndRender());
        statusFilterCombo.addActionListener(e -> applyWorkloadFiltersAndRender());
        moIdFilterCombo.addActionListener(e -> applyWorkloadFiltersAndRender());

        resetFiltersButton.addActionListener(e -> {
            moduleCodeFilterCombo.setSelectedItem("All");
            statusFilterCombo.setSelectedItem("All");
            moIdFilterCombo.setSelectedItem("All");
            applyWorkloadFiltersAndRender();
        });
        refreshButton.addActionListener(e -> refreshAllWorkloadData());
        saveButton.addActionListener(e -> handleWorkloadSaveAction());
        backButton.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));
    }

    private void buildPostPanel(JPanel panel) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.add(openPostsLabel);

        postTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        postTable.setRowHeight(30);
        postTable.getColumnModel().getColumn(P_ACTION_COLUMN_INDEX).setCellRenderer(new PostActionCellRenderer());
        postTable.getColumnModel().getColumn(P_ACTION_COLUMN_INDEX).setCellEditor(new PostActionCellEditor());
        postTable.getColumnModel().getColumn(P_ACTION_COLUMN_INDEX).setPreferredWidth(120);

        JScrollPane tableScrollPane = new JScrollPane(postTable);
        tableScrollPane.setPreferredSize(new Dimension(1220, 500));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton backButton = new JButton("Back");
        JButton refreshButton = new JButton("Refresh");
        JButton saveButton = new JButton("Save");
        bottomPanel.add(backButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(saveButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshPostData());
        saveButton.addActionListener(e -> handlePostSaveAction());
        backButton.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));
    }

    private void refreshAllWorkloadData() {
        allWorkloads = adminService.getAllWorkloads();
        hasUnsavedWorkloadChanges = false;
        rebuildWorkloadFilterOptions();
        applyWorkloadFiltersAndRender();
    }

    private void rebuildWorkloadFilterOptions() {
        String previousModuleCode = (String) moduleCodeFilterCombo.getSelectedItem();
        String previousStatus = (String) statusFilterCombo.getSelectedItem();
        String previousMoId = (String) moIdFilterCombo.getSelectedItem();

        TreeSet<String> moduleCodes = new TreeSet<>();
        TreeSet<String> statuses = new TreeSet<>();
        TreeSet<String> moIds = new TreeSet<>();

        for (AdminWorkload workload : allWorkloads) {
            moduleCodes.add(valueOrEmpty(workload.getModuleCode()));
            statuses.add(valueOrEmpty(workload.getStatus()));
            moIds.add(valueOrEmpty(workload.getMoId()));
        }

        refillCombo(moduleCodeFilterCombo, moduleCodes, previousModuleCode);
        refillCombo(statusFilterCombo, statuses, previousStatus);
        refillCombo(moIdFilterCombo, moIds, previousMoId);
    }

    private void refillCombo(JComboBox<String> combo, TreeSet<String> values, String previousValue) {
        combo.removeAllItems();
        combo.addItem("All");
        for (String value : values) {
            combo.addItem(value);
        }

        if (previousValue != null && values.contains(previousValue)) {
            combo.setSelectedItem(previousValue);
        } else {
            combo.setSelectedItem("All");
        }
    }

    private void applyWorkloadFiltersAndRender() {
        String selectedModuleCode = (String) moduleCodeFilterCombo.getSelectedItem();
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();
        String selectedMoId = (String) moIdFilterCombo.getSelectedItem();

        boolean filterByModuleCode = selectedModuleCode != null && !"All".equals(selectedModuleCode);
        boolean filterByStatus = selectedStatus != null && !"All".equals(selectedStatus);
        boolean filterByMoId = selectedMoId != null && !"All".equals(selectedMoId);

        List<AdminWorkload> filtered = new ArrayList<>();
        for (AdminWorkload workload : allWorkloads) {
            boolean matchModuleCode = !filterByModuleCode || selectedModuleCode.equals(valueOrEmpty(workload.getModuleCode()));
            boolean matchStatus = !filterByStatus || selectedStatus.equals(valueOrEmpty(workload.getStatus()));
            boolean matchMoId = !filterByMoId || selectedMoId.equals(valueOrEmpty(workload.getMoId()));
            if (matchModuleCode && matchStatus && matchMoId) {
                filtered.add(workload);
            }
        }

        renderWorkloads(filtered);
    }

    private void renderWorkloads(List<AdminWorkload> workloads) {
        currentDisplayedWorkloads = workloads;
        totalTAsLabel.setText("Total Active TAs: " + adminService.getTotalActiveTAs(workloads));
        totalModulesLabel.setText("Total Assigned Modules: " + adminService.getTotalAssignedModules(workloads));
        overloadedLabel.setText("Overloaded TAs: " + adminService.getOverloadedCount(workloads));
        overloadedLabel.setHorizontalAlignment(SwingConstants.LEFT);

        workloadTableModel.setRowCount(0);
        for (AdminWorkload workload : workloads) {
            Object[] row = {
                    workload.getMoName(),
                    workload.getMoId(),
                    workload.getTaId(),
                    workload.getTaName(),
                    workload.getModuleName(),
                    workload.getModuleCode(),
                    workload.getCourseWorkHour(),
                    workload.getTaTotalWorkHour(),
                    workload.getStatus(),
                    "Reassign"
            };
            workloadTableModel.addRow(row);
        }
    }

    private void handleWorkloadReassignAction(int rowIndex) {
        String status = String.valueOf(workloadTableModel.getValueAt(rowIndex, W_STATUS_COLUMN_INDEX));
        if (!"Overloaded".equalsIgnoreCase(status)) {
            return;
        }
        if (rowIndex < 0 || rowIndex >= currentDisplayedWorkloads.size()) {
            return;
        }

        AdminWorkload target = currentDisplayedWorkloads.get(rowIndex);
        target.setStatus("Reassigning");
        adminService.recalculateTotalsAndStatusesInMemory(allWorkloads);
        hasUnsavedWorkloadChanges = true;
        applyWorkloadFiltersAndRender();
    }

    private void handleWorkloadSaveAction() {
        if (!hasUnsavedWorkloadChanges) {
            JOptionPane.showMessageDialog(this, "No changes to save.", "Save", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Save all current changes to CSV?",
                "Confirm Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = adminService.saveAllWorkloads(allWorkloads);
        if (success) {
            hasUnsavedWorkloadChanges = false;
            JOptionPane.showMessageDialog(this, "Saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
            refreshAllWorkloadData();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed.", "Save", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshPostData() {
        currentPosts = adminService.getAllPosts();
        hasUnsavedPostChanges = false;
        renderPosts();
    }

    private void renderPosts() {
        openPostsLabel.setText("Open Posts: " + adminService.countOpenPosts(currentPosts));
        openPostsLabel.setHorizontalAlignment(SwingConstants.LEFT);

        postTableModel.setRowCount(0);
        for (AdminRecruitment post : currentPosts) {
            Object[] row = {
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
                    post.getDisplayStatus(),
                    "Action"
            };
            postTableModel.addRow(row);
        }
    }

    private void handleCloseAction(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= currentPosts.size()) {
            return;
        }
        AdminRecruitment post = currentPosts.get(rowIndex);
        if (!post.isOpen()) {
            return;
        }
        post.setOpen(false);
        hasUnsavedPostChanges = true;
        renderPosts();
    }

    private void handleOpenAction(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= currentPosts.size()) {
            return;
        }
        AdminRecruitment post = currentPosts.get(rowIndex);
        if (post.isOpen()) {
            return;
        }
        post.setOpen(true);
        hasUnsavedPostChanges = true;
        renderPosts();
    }

    private void handlePostSaveAction() {
        if (!hasUnsavedPostChanges) {
            JOptionPane.showMessageDialog(this, "No changes to save.", "Save", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Save all current changes to CSV?",
                "Confirm Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = adminService.saveAllPosts(currentPosts);
        if (success) {
            hasUnsavedPostChanges = false;
            JOptionPane.showMessageDialog(this, "Saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
            refreshPostData();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed.", "Save", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private static class WorkloadStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value == null ? "" : String.valueOf(value);
            if ("Overloaded".equalsIgnoreCase(status)) {
                component.setForeground(new Color(200, 0, 0));
            } else if (isSelected) {
                component.setForeground(table.getSelectionForeground());
            } else {
                component.setForeground(table.getForeground());
            }
            return component;
        }
    }

    private class WorkloadActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton actionButton;

        WorkloadActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
            actionButton = new JButton("Reassign");
            add(actionButton);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            int modelRow = table.convertRowIndexToModel(row);
            String status = String.valueOf(workloadTableModel.getValueAt(modelRow, W_STATUS_COLUMN_INDEX));
            actionButton.setEnabled("Overloaded".equalsIgnoreCase(status));
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    private class WorkloadActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton actionButton;
        private int editingRow = -1;
        private boolean actionable;

        WorkloadActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            actionButton = new JButton("Reassign");
            actionButton.addActionListener(e -> {
                stopCellEditing();
                if (actionable && editingRow >= 0) {
                    handleWorkloadReassignAction(editingRow);
                }
            });
            panel.add(actionButton);
        }

        @Override
        public Object getCellEditorValue() {
            return "Reassign";
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            editingRow = table.convertRowIndexToModel(row);
            String status = String.valueOf(workloadTableModel.getValueAt(editingRow, W_STATUS_COLUMN_INDEX));
            actionable = "Overloaded".equalsIgnoreCase(status);
            actionButton.setEnabled(actionable);
            return panel;
        }
    }

    private class PostActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton actionButton;

        PostActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
            actionButton = new JButton("Action");
            add(actionButton);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            int modelRow = table.convertRowIndexToModel(row);
            String status = String.valueOf(postTableModel.getValueAt(modelRow, P_STATUS_COLUMN_INDEX));
            actionButton.setText("Open".equalsIgnoreCase(status) ? "Close" : "Open");
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    private class PostActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton actionButton;
        private int editingRow = -1;
        private String currentAction = "CLOSE";

        PostActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            actionButton = new JButton("Action");
            actionButton.addActionListener(e -> {
                stopCellEditing();
                if (editingRow >= 0) {
                    if ("OPEN".equals(currentAction)) {
                        handleOpenAction(editingRow);
                    } else {
                        handleCloseAction(editingRow);
                    }
                }
            });
            panel.add(actionButton);
        }

        @Override
        public Object getCellEditorValue() {
            return "Action";
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            editingRow = table.convertRowIndexToModel(row);
            String status = String.valueOf(postTableModel.getValueAt(editingRow, P_STATUS_COLUMN_INDEX));
            if ("Open".equalsIgnoreCase(status)) {
                currentAction = "CLOSE";
                actionButton.setText("Close");
            } else {
                currentAction = "OPEN";
                actionButton.setText("Open");
            }
            return panel;
        }
    }
}
