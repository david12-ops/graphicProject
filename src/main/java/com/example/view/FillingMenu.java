package com.example.view;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.example.enums.ColorFillMode;

/**
 * With this component you can choose filling algorithm and color or pattern
 * that will be used for filling.
 */
public class FillingMenu {

    private final Panel panel;
    private final JMenu fillingMenu = new JMenu("Filling menu");

    private ColorFillMode colorFillMode = ColorFillMode.CONSTANT;

    public FillingMenu(Panel panel) {
        this.panel = panel;

        fillingMenu.getAccessibleContext().setAccessibleDescription(
                "Menu for picking filling color");

        // ---- radio buttons ----
        fillingMenu.addSeparator();
        ButtonGroup toolGroup = new ButtonGroup();

        JRadioButtonMenuItem scanLine = new JRadioButtonMenuItem("Constant", true);
        scanLine.addActionListener(e -> {
            colorFillMode = ColorFillMode.CONSTANT;
            onMenuChanged();
        });

        toolGroup.add(scanLine);
        fillingMenu.add(scanLine);

        JRadioButtonMenuItem seedFill = new JRadioButtonMenuItem("Gradient");
        seedFill.addActionListener(e -> {
            colorFillMode = ColorFillMode.GRADIENT;
            onMenuChanged();
        });

        toolGroup.add(seedFill);
        fillingMenu.add(seedFill);

        JRadioButtonMenuItem seedFillBorder = new JRadioButtonMenuItem("Texture");
        seedFillBorder.addActionListener(e -> {
            colorFillMode = ColorFillMode.TEXTURE;
            onMenuChanged();
        });

        toolGroup.add(seedFillBorder);
        fillingMenu.add(seedFillBorder);
    }

    private void onMenuChanged() {
        panel.setColorFillMode(colorFillMode);

        panel.repaint();
    }

    public JMenu getJmenu() {
        return fillingMenu;
    }
}