package com.example.controller;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.SwingUtilities;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.fill.SeedFillBorder;
import com.example.fill.SeedFiller;
import com.example.model.Line;
import com.example.model.Point;
import com.example.model.Polygon;
import com.example.raster.Raster;
import com.example.raster.RasterBufferedImage;
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
    /*
     * TODO – connecting polygons, snapping points, deleting points while holding
     * Shift
     * TODO – unable to display the polygon sometimes immediately after clicking (it
     * appears sometimes only after dragging)
     * TODO – improve clipping
     * TODO – the scan-line algorithm does not work completely correctly
     * TODO – drawing a rectangle as an entity in the model. The controller in its
     * current form is not able to meaningfully contain so many functions
     * without modes, and a runnable application would not be able
     * to switch modes without user interaction and functions that
     * would implement it.
     * TODO – also filling can have some mode to use all filling algorithms
     * (seed fill/border fill, scan-line)
     */

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

        /**
         * Handles mouse press events:
         * - Left click starts line drawing
         * - Right click selects or removes polygon vertices
         * - Middle click performs seed fill
         */
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    List<Color> setColors = lineRasterizer.getColors();
                    Raster ptRaster = createPatternRaster(100, 100);

                    /*
                     * Scan-line algorithm is more ralible with gradient then seedFill and
                     * seedFillBorder
                     */
                    // with pattern
                    // ScanLine scanLine = new ScanLine(panel.getRaster(), ptRaster);
                    // scanLine.fill(polygon);
                    // update();
                    // return;

                    // with color
                    // ScanLine scanLine = new ScanLine(panel.getRaster(), 0xFFA52A2A);
                    // scanLine.fill(polygon);
                    // update();
                    // return;

                    // with color
                    // SeedFiller seedFill = new SeedFill(panel.getRaster(),
                    // panel.getRaster().getPixel(e.getX(), e.getY()), 0xFFA52A2A, e.getX(),
                    // e.getY());
                    // seedFill.fill();
                    // update();
                    // return;

                    // with pattern
                    // SeedFiller seedFill = new SeedFill(panel.getRaster(), ptRaster,
                    // panel.getRaster().getPixel(e.getX(), e.getY()), e.getX(), e.getY());
                    // seedFill.fill();
                    // update();
                    // return;

                    // with color
                    // SeedFiller seedFillBorder = new SeedFillBorder(panel.getRaster(),
                    // colors.get(0).getRGB(), 0xFFA52A2A, e.getX(),
                    // e.getY());
                    // seedFillBorder.fill();
                    // update();
                    // return;

                    // with pattern
                    SeedFiller seedFillBorder = new SeedFillBorder(panel.getRaster(), ptRaster,
                            setColors.get(0).getRGB(),
                            e.getX(), e.getY());
                    seedFillBorder.fill();
                    update();
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
                lineRasterizer.setRasterizeMode(e.isShiftDown() ? RasterizerMode.SHIFT : RasterizerMode.NORMAL);

                Point end = new Point(e.getX(), e.getY());
                previewPoint = end;

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

    /**
     * Creates a checkerboard pattern raster.
     * 
     * The pattern alternates between light gray and dark gray pixels
     * based on the parity of the sum of pixel coordinates.
     * This raster can be used for pattern filling.
     *
     * @param width  width of the pattern raster
     * @param height height of the pattern raster
     * @return generated pattern raster, or {@code null} if width or height is
     *         negative
     */
    private Raster createPatternRaster(int width, int height) {

        if (width < 0 || height < 0)
            return null;

        Raster patternRaster = new RasterBufferedImage(width, height);

        int lightGray = 0xDDDDDD;
        int darkGray = 0x777777;

        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                if ((px + py) % 2 == 0) {
                    patternRaster.setPixel(px, py, lightGray);
                } else {
                    patternRaster.setPixel(px, py, darkGray);
                }
            }
        }

        return patternRaster;
    }
}
