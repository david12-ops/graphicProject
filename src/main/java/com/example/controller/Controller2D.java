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
    // TODO - spojení polygonu pri shift
    // TODO - neumí ze zacatku po klikaní zobrazit polygon (zobrazí až po tažení)
    // TODO - nespojuje podle nejbližšího okolního bodu

    private final Panel panel;
    private RasterizerMode mode;

    private LineRasterizer lineRasterizer;
    private PolygonRasterizer polygonRasterizer;

    private Polygon polygon;

    private Point pressedPoint;
    private Point pendingPoint;
    private Point previewPoint;

    private Point draggedVertex;

    private boolean dragging;

    private static final int PICK_RADIUS = 50;
    private static final int DRAG_THRESHOLD = 3;
    private static final int DRAG_THRESHOLD_SQUARE = DRAG_THRESHOLD * DRAG_THRESHOLD;

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

                if (SwingUtilities.isMiddleMouseButton(e)) {
                    SeedFill seedFill = new SeedFill(
                            panel.getRaster(), panel.getRaster().getPixel(e.getX(), e.getY()),
                            e.getX(), e.getY());
                    seedFill.fill();
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragging = false;
                    pressedPoint = new Point(e.getX(), e.getY());
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    if (e.isShiftDown()) {
                        Point nearesPoint = polygon.getNearesPoint(e.getX(), e.getY(), PICK_RADIUS);

                        if (nearesPoint != null && polygon.getSize() > 3) {
                            polygon.removePoint(nearesPoint);
                            redraw();
                        }
                        return;
                    }

                    draggedVertex = polygon.getNearesPoint(e.getX(), e.getY(), PICK_RADIUS);
                    return;
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    draggedVertex = null;
                    return;
                }

                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                if (dragging) {
                    Point end = new Point(e.getX(), e.getY());

                    if (pendingPoint == null) {
                        polygon.addPoint(pressedPoint);
                    }

                    polygon.addPoint(end);
                    pendingPoint = end;
                } else {
                    polygon.addPoint(pressedPoint);
                    pendingPoint = pressedPoint;
                }

                clearPreview();
                redraw();
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

                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                dragging = isPressed(pressedPoint, new Point(e.getX(), e.getY()));

                if (dragging) {
                    Point end = new Point(e.getX(), e.getY());
                    previewPoint = end;

                    if (e.isShiftDown()) {
                        mode = RasterizerMode.SHIFT;
                    } else {
                        mode = RasterizerMode.NORMAL;
                    }

                    lineRasterizer.setRasterizeMode(mode);

                    panel.clear();
                    polygonRasterizer.rasterize(polygon);
                    Line preview = new Line(pendingPoint != null ? pendingPoint : pressedPoint, previewPoint);
                    lineRasterizer.rasterize(preview);
                    update();
                }
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

    private boolean isPressed(Point pressedPoint, Point currenPoint) {
        int dx = currenPoint.getX() - pressedPoint.getX();
        int dy = currenPoint.getY() - pressedPoint.getY();

        if (dx * dx + dy * dy > DRAG_THRESHOLD_SQUARE) {
            return true;
        } else {
            return false;
        }
    }

    private void redraw() {
        panel.clear();
        polygonRasterizer.rasterize(polygon);
        update();
    }

    private void clearPreview() {
        previewPoint = null;
        dragging = false;
    }
}
