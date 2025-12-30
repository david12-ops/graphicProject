package com.example.controller;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.example.enums.ColorMode;
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
    private Line line;
    private LineRasterizer lineRasterizer;

    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    // TODO BUG in deleteing polygon
    // TODO vyresit problem s vykreslovanim polygonu (furt se poji s prvnim bodem) a
    // platnem
    // TODO vykreslovani svisle, vodorovne a uhlopricne cary neni na sto pro

    public void initObjects(Raster raster) {
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
                    // lineRasterizer.setShifMode(true);
                    // polygon.addPoint(new Point(e.getX(), e.getY()));
                    // polygonRasterizer.rasterize(polygon);
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    // TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // SeedFill seedFill = new SeedFill(
                    // panel.getRaster(),
                    // panel.getRaster().getPixel(e.getX(), e.getY()),
                    // e.getX(), e.getY());
                    // seedFill.fill();

                }
                update();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        panel.clear();
                        // lineRasterizer.setShifMode(false);
                        if (polygon.getSize() > 0)
                            polygon.clearAllPoints();

                        line = new Line(
                                panel.getRaster().getWidth() / 2,
                                panel.getRaster().getHeight() / 2,
                                e.getX(), e.getY(),
                                0xff0000);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        // lineRasterizer.setShifMode(true);
                        // if (polygon.getSize() > 0)
                        // polygon.clearAllPoints();

                        // line = new Line(
                        // panel.getRaster().getWidth() / 2,
                        // panel.getRaster().getHeight() / 2,
                        // e.getX(), e.getY(),
                        // 0xff0000);

                    }

                }
                update();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (line != null) {
                    line = new Line(new Point(line.getX1(), line.getY1()), new Point(e.getX(), e.getY()), 0xff0000);
                    lineRasterizer.rasterize(line);
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown())
                    return;

                if (e.isShiftDown()) {
                    lineRasterizer.setShifMode(true);
                    line = new Line(
                            panel.getRaster().getWidth() / 2,
                            panel.getRaster().getHeight() / 2,
                            e.getX(), e.getY(),
                            0xff0000);
                    lineRasterizer.rasterize(line);
                    panel.clear();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    lineRasterizer.setShifMode(true);
                    line = new Line(
                            panel.getRaster().getWidth() / 2,
                            panel.getRaster().getHeight() / 2,
                            e.getX(), e.getY(),
                            0xff0000);
                    lineRasterizer.rasterize(line);

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
        lineRasterizer.setShifMode(false);
    }
}
