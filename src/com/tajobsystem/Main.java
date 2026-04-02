package com.tajobsystem;

import com.tajobsystem.ui.TAJobsFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TAJobsFrame frame = new TAJobsFrame();
            frame.setVisible(true);
        });
    }
}