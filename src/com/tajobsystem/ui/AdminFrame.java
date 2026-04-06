package com.tajobsystem.ui;

import com.tajobsystem.model.Admin;
import com.tajobsystem.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminFrame extends JFrame {
    private static final int STATUS_COLUMN_INDEX = 11;
    private static final int ACTION_COLUMN_INDEX = 12;
    private static final String CARD_DIRECTION = "DIRECTION";
    private static final String CARD_ADMIN = "ADMIN";

    private final AdminService adminService;
    private final JLabel openPostsLabel;
    private final DefaultTableModel tableModel;
    private final JTable postTable;
    private final CardLayout cardLayout;
    private final JPanel rootPanel;

    private List<Admin> currentPosts;
    private boolean hasUnsavedChanges;

    public AdminFrame() {
        this.adminService = new AdminService();
        this.currentPosts = new ArrayList<>();
        this.hasUnsavedChanges = false;
        this.cardLayout = new CardLayout();
        this.rootPanel = new JPanel(cardLayout);

        setTitle("Close Recruitment Channel Demo");
        setSize(1280, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        openPostsLabel = new JLabel("Open Posts: 0");

        String[] columns = {
                "Job ID",
                "Title",
                "Subject",
                "Work Type",
                "Department",
                "Description",
                "Requirements",
                "Open Positions",
                "Deadline",
                "Hours/Week",
                "Compensation",
                "Status",
                "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == ACTION_COLUMN_INDEX;
            }
        };

        postTable = new JTable(tableModel);
        postTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        postTable.setRowHeight(30);
        postTable.getColumnModel().getColumn(ACTION_COLUMN_INDEX).setCellRenderer(new ActionCellRenderer());
        postTable.getColumnModel().getColumn(ACTION_COLUMN_INDEX).setCellEditor(new ActionCellEditor());
        postTable.getColumnModel().getColumn(ACTION_COLUMN_INDEX).setPreferredWidth(120);

        JScrollPane tableScrollPane = new JScrollPane(postTable);
        tableScrollPane.setPreferredSize(new Dimension(1220, 430));

        rootPanel.add(createDirectionPanel(), CARD_DIRECTION);
        rootPanel.add(createAdminPanel(tableScrollPane), CARD_ADMIN);
        setContentPane(rootPanel);
        showDirectionPanel();
    }

    private JPanel createDirectionPanel() {
        JPanel directionPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JButton enterButton = new JButton("Enter Close Post Management");
        enterButton.addActionListener(e -> showAdminPanel());
        centerPanel.add(enterButton);
        directionPanel.add(centerPanel, BorderLayout.CENTER);
        return directionPanel;
    }

    private JPanel createAdminPanel(JScrollPane tableScrollPane) {
        JPanel adminPanel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.add(openPostsLabel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton backButton = new JButton("Back");
        JButton refreshButton = new JButton("Refresh");
        JButton saveButton = new JButton("Save");
        bottomPanel.add(backButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(saveButton);

        backButton.addActionListener(e -> showDirectionPanel());
        refreshButton.addActionListener(e -> refreshData());
        saveButton.addActionListener(e -> handleSaveAction());

        adminPanel.add(topPanel, BorderLayout.NORTH);
        adminPanel.add(tableScrollPane, BorderLayout.CENTER);
        adminPanel.add(bottomPanel, BorderLayout.SOUTH);
        return adminPanel;
    }

    private void showDirectionPanel() {
        cardLayout.show(rootPanel, CARD_DIRECTION);
    }

    private void showAdminPanel() {
        cardLayout.show(rootPanel, CARD_ADMIN);
        refreshData();
    }

    private void refreshData() {
        currentPosts = adminService.getAllPosts();
        hasUnsavedChanges = false;
        renderCurrentPosts();
    }

    private void renderCurrentPosts() {
        openPostsLabel.setText("Open Posts: " + adminService.countOpenPosts(currentPosts));
        openPostsLabel.setHorizontalAlignment(SwingConstants.LEFT);

        tableModel.setRowCount(0);
        for (Admin post : currentPosts) {
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
            tableModel.addRow(row);
        }
    }

    private void handleCloseAction(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= currentPosts.size()) {
            return;
        }
        Admin post = currentPosts.get(rowIndex);
        if (!post.isOpen()) {
            return;
        }
        post.setOpen(false);
        hasUnsavedChanges = true;
        renderCurrentPosts();
    }

    private void handleOpenAction(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= currentPosts.size()) {
            return;
        }
        Admin post = currentPosts.get(rowIndex);
        if (post.isOpen()) {
            return;
        }
        post.setOpen(true);
        hasUnsavedChanges = true;
        renderCurrentPosts();
    }

    private void handleSaveAction() {
        if (!hasUnsavedChanges) {
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
            hasUnsavedChanges = false;
            JOptionPane.showMessageDialog(this, "Saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed.", "Save", JOptionPane.WARNING_MESSAGE);
        }
    }

    private class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton actionButton;

        ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
            actionButton = new JButton("Action");
            add(actionButton);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            int modelRow = table.convertRowIndexToModel(row);
            String status = String.valueOf(tableModel.getValueAt(modelRow, STATUS_COLUMN_INDEX));
            actionButton.setText("Open".equalsIgnoreCase(status) ? "Close" : "Open");

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton actionButton;
        private int editingRow = -1;
        private String currentAction = "CLOSE";

        ActionCellEditor() {
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
            String status = String.valueOf(tableModel.getValueAt(editingRow, STATUS_COLUMN_INDEX));
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


