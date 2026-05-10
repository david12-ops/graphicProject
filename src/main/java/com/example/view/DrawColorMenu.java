package com.example.view;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.example.enums.ColorDrawMode;

public class DrawColorMenu {

    private final Panel panel;
    private final JMenu drawingColorMenu = new JMenu("Drawing color menu");

    private ColorDrawMode colorDrawingMode = ColorDrawMode.SOLID;

    public DrawColorMenu(Panel panel) {
        this.panel = panel;

        drawingColorMenu.getAccessibleContext().setAccessibleDescription(
                "Menu for picking drawing color");

        // ---- radio buttons ----
        drawingColorMenu.addSeparator();
        ButtonGroup colorGroup = new ButtonGroup();

        JRadioButtonMenuItem solidColor = new JRadioButtonMenuItem("solid color", true);
        solidColor.addActionListener(e -> {
            colorDrawingMode = ColorDrawMode.SOLID;
            onMenuChanged();
        });

        colorGroup.add(solidColor);
        drawingColorMenu.add(solidColor);

        JRadioButtonMenuItem seedFill = new JRadioButtonMenuItem("gradient color");
        seedFill.addActionListener(e -> {
            colorDrawingMode = ColorDrawMode.GRADIENT;
            onMenuChanged();
        });

        colorGroup.add(seedFill);
        drawingColorMenu.add(seedFill);
    }

    private void onMenuChanged() {
        panel.setColorDrawMode(colorDrawingMode);

        panel.repaint();
    }

    public JMenu getJmenu() {
        return drawingColorMenu;
    }
}
