package com.example.view;

import java.awt.BorderLayout;

import javax.swing.*;

public class Window extends JFrame {
    private final Panel panel;

    public Window() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("2D Graphics Project");
        setVisible(true);

        panel = new Panel();

        add(panel, BorderLayout.CENTER);
        setVisible(true);
        pack();

        setLocationRelativeTo(null);

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším
        // UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice

    }

    public Panel getPanel() {
        return panel;
    }
}
