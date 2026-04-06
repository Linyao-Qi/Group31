package com.tajobsystem.ui;

import com.tajobsystem.model.Admin;
import com.tajobsystem.service.AdminService;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class AdminFrame extends JFrame {
    private static final int STATUS_COL = 11;
    private static final int ACTION_COL = 12;

    private final AdminService service;
    private final JPanel directionPanel;
    private final JPanel closePostPanel;
    private final JLabel openCountLabel;
    private final DefaultTableModel postsTableModel;
    private final JTable postsTable;

    private List<Admin> draftPosts;
    private boolean draftChanged;

    public AdminFrame() {
        this.service = new AdminService();
        this.draftPosts = new ArrayList<>();
        this.draftChanged = false;

        setTitle("Module Direction");
        setSize(520, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        directionPanel = new JPanel();
        directionPanel.setLayout(new BoxLayout(directionPanel, BoxLayout.Y_AXIS));
        directionPanel.setBorder(BorderFactory.createEmptyBorder(70, 0, 70, 0));

        JButton closePostButton = new JButton("Open ClosePost");
        Dimension buttonSize = new Dimension(210, 38);
        closePostButton.setMaximumSize(buttonSize);
        closePostButton.setPreferredSize(buttonSize);
        closePostButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        closePostButton.addActionListener(e -> showClosePostModule());

        directionPanel.add(Box.createVerticalGlue());
        directionPanel.add(closePostButton);
        directionPanel.add(Box.createVerticalGlue());

        closePostPanel = new JPanel(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        openCountLabel = new JLabel("Open Posts: 0");
        headerPanel.add(openCountLabel);

        String[] columnNames = {
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

        postsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == ACTION_COL;
            }
        };

        postsTable = new JTable(postsTableModel);
        postsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        postsTable.setRowHeight(30);
        postsTable.getColumnModel().getColumn(ACTION_COL).setCellRenderer(new ActionCellRenderer());
        postsTable.getColumnModel().getColumn(ACTION_COL).setCellEditor(new ActionCellEditor());
        postsTable.getColumnModel().getColumn(ACTION_COL).setPreferredWidth(120);

        JScrollPane listScrollPane = new JScrollPane(postsTable);
        listScrollPane.setPreferredSize(new Dimension(1220, 430));

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton refreshBtn = new JButton("Refresh");
        JButton saveBtn = new JButton("Save");
        footerPanel.add(refreshBtn);
        footerPanel.add(saveBtn);
        refreshBtn.addActionListener(e -> refreshData());
        saveBtn.addActionListener(e -> handleSaveAction());

        closePostPanel.add(headerPanel, BorderLayout.NORTH);
        closePostPanel.add(listScrollPane, BorderLayout.CENTER);
        closePostPanel.add(footerPanel, BorderLayout.SOUTH);

        add(directionPanel, BorderLayout.CENTER);
    }

    private void showClosePostModule() {
        setTitle("Close Recruitment Channel Demo");
        setSize(1280, 580);
        getContentPane().removeAll();
        add(closePostPanel, BorderLayout.CENTER);
        refreshData();
        revalidate();
        repaint();
    }

    private void refreshData() {
        draftPosts = service.getAllPosts();
        draftChanged = false;
        renderCurrentPosts();
    }

    private void renderCurrentPosts() {
        openCountLabel.setText("Open Posts: " + service.countOpenPosts(draftPosts));
        openCountLabel.setHorizontalAlignment(SwingConstants.LEFT);

        postsTableModel.setRowCount(0);
        for (Admin postItem : draftPosts) {
            Object[] rowData = {
                    postItem.getJobId(),
                    postItem.getTitle(),
                    postItem.getSubject(),
                    postItem.getWorkType(),
                    postItem.getDepartment(),
                    postItem.getDescription(),
                    postItem.getRequirements(),
                    postItem.getOpenPositions(),
                    postItem.getDeadline(),
                    postItem.getHoursPerWeek(),
                    postItem.getCompensation(),
                    postItem.getDisplayStatus(),
                    "Action"
            };
            postsTableModel.addRow(rowData);
        }
    }

    private void handleCloseAction(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= draftPosts.size()) {
            return;
        }
        Admin targetPost = draftPosts.get(rowIndex);
        if (!targetPost.isOpen()) {
            return;
        }
        targetPost.setOpen(false);
        draftChanged = true;
        renderCurrentPosts();
    }

    private void handleOpenAction(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= draftPosts.size()) {
            return;
        }
        Admin targetPost = draftPosts.get(rowIndex);
        if (targetPost.isOpen()) {
            return;
        }
        targetPost.setOpen(true);
        draftChanged = true;
        renderCurrentPosts();
    }

    private void handleSaveAction() {
        if (!draftChanged) {
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

        boolean success = service.saveAllPosts(draftPosts);
        if (success) {
            draftChanged = false;
            JOptionPane.showMessageDialog(this, "Saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed.", "Save", JOptionPane.WARNING_MESSAGE);
        }
    }

    private class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton toggleBtn;

        ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
            toggleBtn = new JButton("Action");
            add(toggleBtn);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            int modelRow = table.convertRowIndexToModel(row);
            String statusText = String.valueOf(postsTableModel.getValueAt(modelRow, STATUS_COL));
            toggleBtn.setText("Open".equalsIgnoreCase(statusText) ? "Close" : "Open");

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel editorContainer;
        private final JButton actionBtn;
        private int editingModelRow = -1;
        private String pendingAction = "CLOSE";

        ActionCellEditor() {
            editorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            actionBtn = new JButton("Action");
            actionBtn.addActionListener(e -> {
                stopCellEditing();
                if (editingModelRow >= 0) {
                    if ("OPEN".equals(pendingAction)) {
                        handleOpenAction(editingModelRow);
                    } else {
                        handleCloseAction(editingModelRow);
                    }
                }
            });
            editorContainer.add(actionBtn);
        }

        @Override
        public Object getCellEditorValue() {
            return "Action";
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            editingModelRow = table.convertRowIndexToModel(row);
            String statusText = String.valueOf(postsTableModel.getValueAt(editingModelRow, STATUS_COL));
            if ("Open".equalsIgnoreCase(statusText)) {
                pendingAction = "CLOSE";
                actionBtn.setText("Close");
            } else {
                pendingAction = "OPEN";
                actionBtn.setText("Open");
            }
            return editorContainer;
        }
    }
}
