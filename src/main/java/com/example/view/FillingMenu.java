package com.example.view;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.example.enums.FillColorMode;
import com.example.enums.FillTool;

/**
 * With this component you can choose filling algorithm and color or pattern
 * that will be used for filling.
 */
public class FillingMenu {

    private final Panel panel;
    private final JMenu fillingMenu = new JMenu("Filling menu");

    private FillTool fillTool = FillTool.SCANLINE;
    private FillColorMode fillColorMode = FillColorMode.PATTERN;

    public FillingMenu(Panel panel) {
        this.panel = panel;

        // Create menu
        fillingMenu.getAccessibleContext().setAccessibleDescription(
                "Menu for picking filling algorithm and color/pattern");

        // ---- radio buttons ----
        fillingMenu.addSeparator();
        ButtonGroup toolGroup = new ButtonGroup();

        JRadioButtonMenuItem scanLine = new JRadioButtonMenuItem("Scan-line", true);
        scanLine.addActionListener(e -> {
            fillTool = FillTool.SCANLINE;
            onMenuChanged();
        });

        toolGroup.add(scanLine);
        fillingMenu.add(scanLine);

        JRadioButtonMenuItem seedFill = new JRadioButtonMenuItem("Seed fill");
        seedFill.addActionListener(e -> {
            fillTool = FillTool.SEEDFILL;
            onMenuChanged();
        });

        toolGroup.add(seedFill);
        fillingMenu.add(seedFill);

        JRadioButtonMenuItem seedFillBorder = new JRadioButtonMenuItem("Seed fill border");
        seedFillBorder.addActionListener(e -> {
            fillTool = FillTool.SEEDFILLBORDER;
            onMenuChanged();
        });

        toolGroup.add(seedFillBorder);
        fillingMenu.add(seedFillBorder);

        // ---- radio buttons ----
        fillingMenu.addSeparator();
        ButtonGroup fillModeGroup = new ButtonGroup();

        JRadioButtonMenuItem pattern = new JRadioButtonMenuItem("Fill with pattern", true);
        pattern.addActionListener(e -> {
            fillColorMode = FillColorMode.PATTERN;
            onMenuChanged();
        });

        fillModeGroup.add(pattern);
        fillingMenu.add(pattern);

        JRadioButtonMenuItem color = new JRadioButtonMenuItem("Fill with color");
        color.addActionListener(e -> {
            fillColorMode = FillColorMode.COLOR;
            onMenuChanged();
        });

        fillModeGroup.add(color);
        fillingMenu.add(color);
    }

    private void onMenuChanged() {
        panel.setFillColorMode(fillColorMode);
        panel.setFillTool(fillTool);

        panel.repaint();
    }

    public JMenu getJmenu() {
        return fillingMenu;
    }

    public FillColorMode getFillColorMode() {
        return fillColorMode;
    }

    public FillTool getFillTool() {
        return fillTool;
    }
}