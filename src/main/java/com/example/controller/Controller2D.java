package com.example.controller;

import java.awt.Cursor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.fill.Filler;
import com.example.fill.SeedFill;
import com.example.model.Line;
import com.example.model.Point;
import com.example.model.Polygon;
import com.example.raster.Raster;
import com.example.rasterize.FilledLineRasterizer;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.PolygonRasterizer;
import com.example.view.Panel;

/**
 * Controller responsible for handling user interaction
 * and coordinating rasterization of lines and polygons
 * in a 2D drawing panel.
 */
public class Controller2D implements Controller {
    private final Panel panel;

    private LineRasterizer lineRasterizer;
    private PolygonRasterizer polygonRasterizer;

    private Polygon polygon;

    private Point pressedPoint;
    private Point previewPoint;

    private Point draggedVertex;

    /**
     * Creates a new 2D controller for the given panel.
     * 
     * @param panel Panel used for rendering and input handling
     */
    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    /**
     * Initializes rasterizers and drawable objects.
     *
     * @param raster Raster used for drawing operations
     */
    public void initObjects(Raster raster) {
        // lineRasterizer = new LineRasterizerGraphics(raster);
        lineRasterizer = new FilledLineRasterizer(raster);

        lineRasterizer.setColorMode(ColorMode.SOLID);
        lineRasterizer.setSolidColor(0x00ff00);

        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        polygon = new Polygon();
    }

    /**
     * Registers mouse, keyboard, and component listeners
     * for user interaction with the panel.
     *
     * @param panel Panel to attach listeners to
     */
    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            /**
             * Handles mouse press events:
             * - Left click starts line drawing
             * - Right click selects or removes polygon vertices
             * - Middle click performs seed fill
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    Filler seedFill = new SeedFill(
                            panel.getRaster(), panel.getRaster().getPixel(e.getX(), e.getY()),
                            e.getX(), e.getY());
                    seedFill.fill();
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    pressedPoint = new Point(e.getX(), e.getY());
                    previewPoint = null;
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    if (e.isShiftDown()) {
                        panel.setCursor(Cursor.getDefaultCursor());
                        Point nearesPoint = polygon.getNearesPoint(e.getX(), e.getY());

                        if (nearesPoint != null && polygon.getSize() > 3) {
                            polygon.removePoint(nearesPoint);
                            redraw();
                        }
                        return;
                    }

                    draggedVertex = polygon.getNearesPoint(e.getX(), e.getY());
                    return;
                }

            }

            /**
             * Finalizes drawing or vertex manipulation
             * when the mouse button is released.
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    draggedVertex = null;
                    panel.setCursor(Cursor.getDefaultCursor());
                    return;
                }

                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                if (previewPoint != null) {
                    polygon.addPoint(pressedPoint);
                    polygon.addPoint(previewPoint);
                } else {

                    polygon.addPoint(pressedPoint);
                }

                panel.setCursor(Cursor.getDefaultCursor());

                clearPreview();
                redraw();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {

            /**
             * Handles mouse dragging:
             * - Moves polygon vertices
             * - Draws preview lines
             * - Control press switchs edge color to gradient
             * (ctrl needs to be pressed down in every action for gradient)
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown()) {
                    lineRasterizer.setColorMode(ColorMode.GRADIENT);
                    lineRasterizer.setGradientColors(
                            new java.awt.Color(0xff0000),
                            new java.awt.Color(0x0000ff));
                } else {
                    lineRasterizer.setColorMode(ColorMode.SOLID);
                    lineRasterizer.setSolidColor(0x00ff00);
                }

                if (SwingUtilities.isRightMouseButton(e) && draggedVertex != null) {
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    draggedVertex.set(e.getX(), e.getY());

                    panel.clear();
                    polygonRasterizer.rasterize(polygon);
                    update();
                    return;
                }

                if (!SwingUtilities.isLeftMouseButton(e) || pressedPoint == null) {
                    return;
                }

                // Prewiev
                Point end = new Point(e.getX(), e.getY());
                previewPoint = end;
                lineRasterizer.setRasterizeMode(e.isShiftDown() ? RasterizerMode.SHIFT : RasterizerMode.NORMAL);

                panel.clear();
                polygonRasterizer.rasterize(polygon);
                lineRasterizer.rasterize(new Line(pressedPoint, previewPoint));
                update();
            }
        });

        panel.addKeyListener(new KeyAdapter() {

            /**
             * Handles keyboard shortcuts.
             * Pressing 'C' clears the drawing.
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    hardClear();
                }
            }
        });

        panel.addComponentListener(new ComponentAdapter() {

            /**
             * Reinitializes raster and objects
             * when the panel is resized.
             */
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    /**
     * Repaints the panel.
     */
    private void update() {
        panel.repaint();
    }

    /**
     * Clears the panel and removes all polygon data.
     */
    private void hardClear() {
        panel.clear();
        polygon.clearAllPoints();
        clearPreview();

        draggedVertex = null;

        update();
    }

    /**
     * Redraws the entire polygon and its edges.
     */
    private void redraw() {
        panel.clear();

        int polygonSize = polygon.getSize();

        if (polygonSize >= 2) {
            for (int i = 0; i < polygonSize - 1; i++) {
                lineRasterizer.rasterize(new Line(polygon.getPoint(i), polygon.getPoint(i + 1)));
            }
        }

        polygonRasterizer.rasterize(polygon);

        update();
    }

    /**
     * Clears preview line state.
     */
    private void clearPreview() {
        previewPoint = null;
        pressedPoint = null;
    }
}
