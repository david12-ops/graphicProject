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
    private Point draggedVertex;
    private boolean dragging;

    private static final int PICK_RADIUS = 50;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        lineRasterizer = new FilledLineRasterizer(raster, ColorMode.GRADIENT);
        // lineRasterizer = new LineRasterizerGraphics(raster, ColorMode.GRADIENT);

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

                if (SwingUtilities.isRightMouseButton(e) && e.isShiftDown()) {
                    Point nearesPoint = polygon.getNearesPoint(e.getX(), e.getY(), PICK_RADIUS);

                    if (nearesPoint != null && polygon.getSize() > 3) {
                        polygon.removePoint(nearesPoint);

                        panel.clear();
                        polygonRasterizer.rasterize(polygon);
                        update();
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    dragging = true;
                    startPoint = new Point(e.getX(), e.getY());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    draggedVertex = polygon.getNearesPoint(e.getX(), e.getY(), PICK_RADIUS);
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    SeedFill seedFill = new SeedFill(
                            panel.getRaster(), panel.getRaster().getPixel(e.getX(), e.getY()),
                            e.getX(), e.getY());
                    seedFill.fill();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    draggedVertex = null;
                    return;
                }

                if (!dragging || startPoint == null)
                    return;

                Point end = new Point(e.getX(), e.getY());

                Line finalLine = new Line(startPoint, end);

                lineRasterizer.rasterize(finalLine);

                polygon.addPoint(end);

                dragging = false;
                startPoint = null;

                update();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && draggedVertex != null) {
                    draggedVertex.set(e.getX(), e.getY());

                    panel.clear();
                    polygonRasterizer.rasterize(polygon);
                    update();
                    return;
                }

                if (!dragging || startPoint == null)
                    return;

                Point end = new Point(e.getX(), e.getY());
                ;

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
