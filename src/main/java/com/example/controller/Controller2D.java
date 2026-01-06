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
import com.example.fill.ScanLine;
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
    // TODO - zlepsit orezavani
    // TODO - kreslení obdelníku neumí
    // TODO - malovani pomoci vzoru neumi
    // TODO - scanLine algoritmus nefunguje uplně dobre

    private final Panel panel;

    private LineRasterizer lineRasterizer;
    private PolygonRasterizer polygonRasterizer;

    private Polygon polygon;

    private Point pressedPoint;
    private Point previewPoint;

    private Point draggedVertex;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        lineRasterizer = new FilledLineRasterizer(raster, ColorMode.SOLID);
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
                    // List<Color> colors = lineRasterizer.getColors();

                    // SeedFill seedFill = new SeedFill(
                    // panel.getRaster(), panel.getRaster().getPixel(e.getX(), e.getY()),
                    // 0xFFA52A2A,
                    // e.getX(), e.getY());
                    // seedFill.fill();
                    // update();
                    // return;

                    ScanLine scanLine = new ScanLine(panel.getRaster(), 0xFFA52A2A);
                    scanLine.fill(polygon);
                    update();
                    return;

                    // if (colors.isEmpty()) {
                    // System.out.println("Color mode is invalid or missing colors to draw");
                    // return;
                    // } else if (colors.size() == 1) {
                    // SeedFillBorder seedFillBorder = new SeedFillBorder(panel.getRaster(),
                    // colors.get(0).getRGB(),
                    // 0xFFA52A2A,
                    // e.getX(), e.getY());
                    // seedFillBorder.fill();
                    // update();
                    // return;
                    // }
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    pressedPoint = new Point(e.getX(), e.getY());
                    previewPoint = null;
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    if (e.isShiftDown()) {
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

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    draggedVertex = null;
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
        clearPreview();

        draggedVertex = null;

        update();
    }

    private void redraw() {
        panel.clear();

        int polygonSize = polygon.getSize();

        // draw polyline
        if (polygonSize >= 2) {
            for (int i = 0; i < polygonSize - 1; i++) {
                lineRasterizer.rasterize(new Line(polygon.getPoint(i), polygon.getPoint(i + 1)));
            }
        }

        polygonRasterizer.rasterize(polygon);

        update();
    }

    private void clearPreview() {
        previewPoint = null;
        pressedPoint = null;
    }
}
