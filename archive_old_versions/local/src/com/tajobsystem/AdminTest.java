package com.tajobsystem;

import com.tajobsystem.ui.AdminFrame;

import javax.swing.SwingUtilities;

public class AdminTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminFrame frame = new AdminFrame();
            frame.setVisible(true);
        });
    }
}
