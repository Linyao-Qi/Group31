package com.tajobsystem.ui;

import com.tajobsystem.model.Application;
import com.tajobsystem.model.Job;
import com.tajobsystem.model.TAProfile;
import com.tajobsystem.service.ApplicationService;
import com.tajobsystem.service.ApplicationService.WithdrawResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dialog showing all applications submitted by a TA (history + current status).
 * Allows withdrawal of applications that are still in "Submitted" status.
 */
public class ApplicationStatusFrame extends JDialog {

    private final TAProfile taProfile;
    private final Map<String, Job> jobMap;
    private final ApplicationService service = new ApplicationService();

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton withdrawBtn;

    // Column indices
    private static final int COL_JOB_ID    = 0;
    private static final int COL_TITLE     = 1;
    private static final int COL_STATUS    = 2;
    private static final int COL_CV        = 3;
    private static final int COL_APP_ID    = 4;

    public ApplicationStatusFrame(Frame parent, TAProfile taProfile, List<Job> jobs) {
        super(parent, "My Applications – " + taProfile.getName(), true);
        this.taProfile = taProfile;
        this.jobMap = new HashMap<>();
        for (Job j : jobs) jobMap.put(j.getJobId(), j);
        initUI();
        loadData();
        pack();
        setMinimumSize(new Dimension(750, 300));
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ---- Header label ----
        JLabel header = new JLabel("Application History for: " + taProfile.getName()
                + "  (" + taProfile.getTaId() + ")");
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        add(header, BorderLayout.NORTH);

        // ---- Table ----
        String[] columns = {"Job ID", "Job Title", "Status", "CV Submitted", "Application ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        table.getColumnModel().getColumn(COL_JOB_ID).setPreferredWidth(70);
        table.getColumnModel().getColumn(COL_TITLE).setPreferredWidth(210);
        table.getColumnModel().getColumn(COL_STATUS).setPreferredWidth(100);
        table.getColumnModel().getColumn(COL_CV).setPreferredWidth(150);
        table.getColumnModel().getColumn(COL_APP_ID).setPreferredWidth(220);

        // Color-code the Status column
        table.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new StatusCellRenderer());

        table.getSelectionModel().addListSelectionListener(e -> updateButtons());

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- Buttons ----
        withdrawBtn = new JButton("Withdraw Selected");
        withdrawBtn.setEnabled(false);
        withdrawBtn.addActionListener(e -> handleWithdraw());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(withdrawBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Application> apps = service.getApplicationsByTA(taProfile.getTaId());
        if (apps.isEmpty()) {
            // Leave table empty; the "no data" message is handled by the empty table itself
        }
        for (Application app : apps) {
            Job job = jobMap.get(app.getJobId());
            String title = (job != null) ? job.getTitle() : "(unknown)";
            String cv    = (app.getCvFilePath() != null)
                    ? new File(app.getCvFilePath()).getName() : "None";
            tableModel.addRow(new Object[]{
                app.getJobId(), title, app.getAppStatus(), cv, app.getAppId()
            });
        }
        updateButtons();
    }

    /** Enable Withdraw button only when a "Submitted" row is selected. */
    private void updateButtons() {
        int row = table.getSelectedRow();
        if (row < 0) {
            withdrawBtn.setEnabled(false);
            return;
        }
        String status = (String) tableModel.getValueAt(row, COL_STATUS);
        withdrawBtn.setEnabled("Submitted".equals(status));
    }

    private void handleWithdraw() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String appId    = (String) tableModel.getValueAt(row, COL_APP_ID);
        String jobTitle = (String) tableModel.getValueAt(row, COL_TITLE);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Withdraw your application for:\n  " + jobTitle + "\n\n"
                + "The application will remain in your history as \"Withdrawn\".",
                "Confirm Withdrawal",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        WithdrawResult result = service.withdrawApplication(taProfile.getTaId(), appId);
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    "Application withdrawn successfully.",
                    "Withdrawn", JOptionPane.INFORMATION_MESSAGE);
            loadData();  // refresh table
        } else {
            JOptionPane.showMessageDialog(this,
                    result.message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Renders the Status column with colours: green=Submitted, red=Withdrawn, blue=others. */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String status = value != null ? value.toString() : "";
                switch (status) {
                    case "Submitted":
                        c.setForeground(new Color(0, 128, 0));   // dark green
                        break;
                    case "Withdrawn":
                        c.setForeground(Color.GRAY);
                        break;
                    case "Accepted":
                        c.setForeground(new Color(0, 0, 180));   // dark blue
                        break;
                    case "Rejected":
                        c.setForeground(Color.RED);
                        break;
                    default:
                        c.setForeground(table.getForeground());
                }
            } else {
                c.setForeground(table.getSelectionForeground());
            }
            return c;
        }
    }
}
