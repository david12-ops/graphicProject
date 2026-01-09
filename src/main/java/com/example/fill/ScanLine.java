package com.example.fill;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Line;
import com.example.model.Point;
import com.example.model.Polygon;
import com.example.raster.Raster;

/**
 * Implementation of polygon filling using the Scan-Line algorithm.
 *
 * The algorithm processes the polygon line by line (for each y coordinate),
 * computes intersections of polygon edges with the current scan line,
 * sorts them and fills pixels between pairs of intersections.
 *
 *
 * Supports:
 * solid color filling,
 * pattern filling via {@link PatternPainter}
 */
public class ScanLine extends PatternPainter implements PolygonFiller {

    private Raster raster;

    private List<Line> tempLines = new ArrayList<>();
    private List<Integer> intersections = new ArrayList<>();
    private Color fillColor;

    /**
     * Creates a scan-line filler with a solid fill color.
     *
     * @param raster    Target raster
     * @param fillColor Color used to fill the polygon
     */
    public ScanLine(Raster raster, int fillColor) {
        super(null);
        this.raster = raster;
        this.fillColor = new Color(fillColor);
    }

    /**
     * Creates a scan-line filler with a pattern raster.
     *
     * @param raster        Target raster
     * @param patternRaster Raster used as fill pattern
     */
    public ScanLine(Raster raster, Raster patternRaster) {
        super(patternRaster);
        this.raster = raster;
        this.fillColor = null;
    }

    /**
     * Performs the scan-line polygon filling.
     * 
     * Steps:
     * 
     * Builds a list of polygon edges (ignoring horizontal ones).
     * Finds the vertical range of the polygon.
     * For each scan line:
     * 
     * computes intersections with edges,
     * sorts and filters duplicate intersections,
     * fills pixels between pairs of intersections.
     *
     * @param polygon Polygon to be filled
     */
    private void scanLine(Polygon polygon) {
        int ymin = Integer.MAX_VALUE;
        int ymax = Integer.MIN_VALUE;
        tempLines.clear();

        for (int i = 0; i < polygon.getSize(); i++) {
            Point a = polygon.getPoint(i);
            Point b = polygon.getPoint((i + 1) % polygon.getSize());

            if (a.getY() == b.getY())
                continue;

            Line e = new Line(a, b);
            e.normalize();
            e.compute();

            tempLines.add(e);

            ymin = Math.min(ymin, e.getPointA().getY());
            ymax = Math.max(ymax, e.getPointB().getY());
        }

        for (int y = ymin; y < ymax; y++) {
            intersections.clear();

            for (Line tl : tempLines) {
                if (tl.isIntersection(y)) {
                    intersections.add(tl.intersection(y));
                }
            }

            intersections.sort(Integer::compareTo);

            List<Integer> filtered = new ArrayList<>();
            for (int i = 0; i < intersections.size(); i++) {
                int x = intersections.get(i);

                if (i == 0 || x != intersections.get(i - 1)) {
                    filtered.add(x);
                }
            }

            intersections = filtered;

            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                int x1 = intersections.get(i);
                int x2 = intersections.get(i + 1);

                for (int x = x1; x < x2; x++) {
                    int color = (patternRaster != null && fillColor == null) ? paint(x, y) : fillColor.getRGB();
                    raster.setPixel(x, y, color);
                }
            }
        }
    }

    /**
     * Fills the given polygon using the scan-line algorithm.
     *
     * @param polygon Polygon to fill
     */
    @Override
    public void fill(Polygon polygon) {

        if (fillColor == null && patternRaster == null) {
            System.out.println("Color or pattern for filling is required");
            return;
        }

        scanLine(polygon);
    }
}
