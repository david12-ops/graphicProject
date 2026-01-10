package com.example.view;

import java.awt.BorderLayout;

import javax.swing.*;

public class Window extends JFrame {
    private final Panel panel;
    private Menu menu;

    public Window() {
        panel = new Panel();
        menu = new Menu(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("2D Graphics Project");
        setVisible(true);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
        pack();

        setLocationRelativeTo(null);

        setJMenuBar(menu.getMenuBar());

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším
        // UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice

    }

    public Panel getPanel() {
        return panel;
    }
}
