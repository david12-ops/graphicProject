package com.example.controller;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.example.fill.SeedFill;
import com.example.model.Line;
import com.example.model.Point;
import com.example.model.Polygon;
import com.example.raster.Raster;
import com.example.rasterize.FilledLineRasterizer;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.PolygonRasterizer;
import com.example.view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;
    private LineRasterizer lineRasterizer;

    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        lineRasterizer = new FilledLineRasterizer(raster);
        lineRasterizer.setColor(0x00ff00);

        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        polygon = new Polygon();
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
                    // panel.clear();
                    polygon.addPoint(new Point(e.getX(), e.getY()));
                    panel.getRaster().setPixel(e.getX(), e.getY(), 0xff0000);
                    polygonRasterizer.rasterize(polygon);
                    // panel.repaint();

                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    // TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // panel.clear();
                    SeedFill seedFill = new SeedFill(
                            panel.getRaster(),
                            panel.getRaster().getPixel(e.getX(), e.getY()),
                            e.getX(), e.getY());
                    seedFill.fill();
                    panel.repaint();
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

            @Override
            public void mouseReleased(MouseEvent e) {
                // panel.clear();
                Line line = new Line(
                        panel.getRaster().getWidth() / 2,
                        panel.getRaster().getHeight() / 2,
                        e.getX(), e.getY(),
                        0xff0000);
                lineRasterizer.rasterize(line);
                panel.repaint();
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
                    // panel.clear();
                    Line line = new Line(
                            panel.getRaster().getWidth() / 2,
                            panel.getRaster().getHeight() / 2,
                            e.getX(), e.getY(),
                            0xff0000);
                    lineRasterizer.rasterize(line);
                    panel.repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // TODO
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    // TODO
                }
                // update();

            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    hardClear();
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
    }

    private void hardClear() {
        panel.clear();
    }
}
