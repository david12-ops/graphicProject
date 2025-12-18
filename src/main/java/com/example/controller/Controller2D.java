package com.example.controller;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.example.rasterize.LineRasterizerGraphics;
import com.example.rasterize.Raster;
import com.example.view.Panel;

public class Controller2D implements Controller {

    private Panel panel;

    private int x, y;
    private LineRasterizerGraphics rasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        rasterizer = new LineRasterizerGraphics(raster);
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown())
                    return;

                if (e.isShiftDown()) {
                    // TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    rasterizer.drawLine(x, y, e.getX(), e.getY());
                    x = e.getX();
                    y = e.getY();
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    // TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // TODO
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // TODO
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        // TODO
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown())
                    return;

                if (e.isShiftDown()) {
                    // TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    // TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // TODO
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    // TODO
                }
                update();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    // TODO
                }
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    private void update() {
        panel.clear();
        // TODO
    }

    public void hardClear() {
        panel.clear();
    }
}
