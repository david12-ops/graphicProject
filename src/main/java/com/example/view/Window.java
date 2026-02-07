package com.example.view;

import java.awt.BorderLayout;

import javax.swing.*;

public class Window extends JFrame {
    private final Panel panel;
    private FillingMenu fillingMenu;
    private DrawColorMenu drawColorMenu;
    private final JMenuBar menuBar = new JMenuBar();

    public Window() {
        panel = new Panel();

        fillingMenu = new FillingMenu(panel);
        drawColorMenu = new DrawColorMenu(panel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("2D Graphics Project");
        setVisible(true);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
        pack();

        setLocationRelativeTo(null);

        menuBar.add(fillingMenu.getJmenu());
        menuBar.add(drawColorMenu.getJmenu());

        setJMenuBar(menuBar);

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším
        // UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice

    }

    public Panel getPanel() {
        return panel;
    }
}
