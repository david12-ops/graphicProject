package com.example;

import javax.swing.SwingUtilities;

import com.example.controller.Controller3D;
import com.example.view.Window;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window();
            new Controller3D(window.getPanel());
            window.setVisible(true);
        });
    }
}