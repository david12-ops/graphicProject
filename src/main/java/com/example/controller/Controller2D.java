package com.example.controller;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
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
    private RasterizerMode mode;

    private LineRasterizer lineRasterizer;
    private PolygonRasterizer polygonRasterizer;

    private Polygon polygon;

    private Point startPoint;
    private Point currentPoint;
    private Point draggedVertex;
    private boolean dragging;

    private static final int PICK_RADIUS = 10;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        // lineRasterizer = new FilledLineRasterizer(raster, ColorMode.GRADIENT);
        lineRasterizer = new FilledLineRasterizer(raster, ColorMode.GRADIENT);

        lineRasterizer.setColor(0x00ff00);
        lineRasterizer.setGradientColors(
                new java.awt.Color(0xff0000),
                new java.awt.Color(0x0000ff));

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
                    startPoint = new Point(e.getX(), e.getY());
                    currentPoint = startPoint;
                    dragging = true;
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    SeedFill seedFill = new SeedFill(
                            panel.getRaster(), panel.getRaster().getPixel(e.getX(), e.getY()),
                            e.getX(), e.getY());
                    seedFill.fill();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    double minDist = Double.MAX_VALUE;

                    for (Point p : polygon.getPoints()) {
                        double dx = e.getX() - p.getX();
                        double dy = e.getY() - p.getY();
                        double dist = dx * dx + dy * dy;

                        if (dist < minDist && dist < PICK_RADIUS * PICK_RADIUS) {
                            minDist = dist;
                            draggedVertex = p;
                        }
                    }
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
                if (!dragging || startPoint == null)
                    return;

                Point end = new Point(e.getX(), e.getY());

                if (SwingUtilities.isRightMouseButton(e))
                    draggedVertex = null;

                Line finalLine = new Line(startPoint, end);

                lineRasterizer.rasterize(finalLine);

                polygon.addPoint(end);

                dragging = false;
                startPoint = null;
                currentPoint = null;

                update();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!dragging || startPoint == null)
                    return;

                Point end = new Point(e.getX(), e.getY());

                if (SwingUtilities.isRightMouseButton(e) && draggedVertex != null) {
                    draggedVertex = new Point(e.getX(), e.getY());

                    panel.clear();
                    polygonRasterizer.rasterize(polygon);
                    update();
                    return;
                }

                if (e.isShiftDown()) {
                    mode = RasterizerMode.SHIFT;
                } else {
                    mode = RasterizerMode.NORMAL;
                }

                lineRasterizer.setRasterizeMode(mode);

                panel.clear();
                polygonRasterizer.rasterize(polygon);

                Line preview = new Line(startPoint, end);
                lineRasterizer.rasterize(preview);

                currentPoint = end;
                update();
            }
        });

        panel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
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
        panel.repaint();
    }

    private void hardClear() {
        panel.clear();
        polygon.clearAllPoints();
    }
}
