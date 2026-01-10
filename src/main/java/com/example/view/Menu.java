package com.example.view;

import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

import com.example.enums.FillColorMode;
import com.example.enums.FillTool;

/**
 * With this component you can choose filling algorithm and color or pattern
 * that will be used for filling.
 */
public class Menu {

    private JMenuBar menuBar;
    private FillTool fillTool = FillTool.SCANLINE;
    private FillColorMode fillColorMode = FillColorMode.PATTERN;

    private final Panel panel;

    public Menu(Panel panel) {
        this.panel = panel;
        // Create menu bar FIRST
        menuBar = new JMenuBar();

        // Create menu
        JMenu menu = new JMenu("Filling menu");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "Menu for picking filling algorithm and color/pattern");

        // Add menu to menu bar
        menuBar.add(menu);

        // ---- radio buttons ----
        menu.addSeparator();
        ButtonGroup toolGroup = new ButtonGroup();

        JRadioButtonMenuItem scanLine = new JRadioButtonMenuItem("Scan-line", true);
        scanLine.addActionListener(e -> {
            fillTool = FillTool.SCANLINE;
            onMenuChanged();
        });

        scanLine.setMnemonic(KeyEvent.VK_R);
        toolGroup.add(scanLine);
        menu.add(scanLine);

        JRadioButtonMenuItem seedFill = new JRadioButtonMenuItem("Seed fill");
        seedFill.addActionListener(e -> {
            fillTool = FillTool.SEEDFILL;
            onMenuChanged();
        });
        toolGroup.add(seedFill);
        menu.add(seedFill);

        JRadioButtonMenuItem seedFillBorder = new JRadioButtonMenuItem("Seed fill border");
        seedFillBorder.addActionListener(e -> {
            fillTool = FillTool.SEEDFILLBORDER;
            onMenuChanged();
        });

        toolGroup.add(seedFillBorder);
        menu.add(seedFillBorder);

        // ---- checkboxes ----
        menu.addSeparator();
        ButtonGroup fillModeGroup = new ButtonGroup();

        JRadioButtonMenuItem pattern = new JRadioButtonMenuItem("Fill with pattern", true);
        pattern.addActionListener(e -> {
            fillColorMode = FillColorMode.PATTERN;
            onMenuChanged();
        });

        fillModeGroup.add(pattern);
        menu.add(pattern);

        JRadioButtonMenuItem color = new JRadioButtonMenuItem("Fill with color");
        color.addActionListener(e -> {
            fillColorMode = FillColorMode.COLOR;
            onMenuChanged();
        });

        fillModeGroup.add(color);
        menu.add(color);
    }

    private void onMenuChanged() {
        panel.setFillColorMode(fillColorMode);
        panel.setFillTool(fillTool);

        panel.repaint();
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public FillColorMode getFillColorMode() {
        return fillColorMode;
    }

    public FillTool geFillTool() {
        return fillTool;
    }
}