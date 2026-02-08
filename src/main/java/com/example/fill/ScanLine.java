package com.example.fill;

import java.util.ArrayList;
import java.util.List;

import com.example.model.Line;
import com.example.model.Point;
import com.example.model.Polygon;
import com.example.model.solid.Solid;
import com.example.raster.Raster;
import com.example.transforms.Col;

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
public class ScanLine extends PatternPainter implements SolidFiller, PolygonFiller {

    private Raster raster;

    private List<Line> tempLines = new ArrayList<>();
    private List<Double> intersections = new ArrayList<>();
    private Col fillColor;

    /**
     * Creates a scan-line filler with a solid fill color.
     *
     * @param raster    Target raster
     * @param fillColor Color used to fill the polygon
     */
    public ScanLine(Raster raster, Col fillColor) {
        super(null);
        this.raster = raster;
        this.fillColor = fillColor;
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
     * Sorts the intersection x-coordinates using the insertion sort algorithm.
     * 
     * The list is sorted in ascending order (from left to right),
     * which is suitable for scan-line polygon filling.
     *
     * @param intersections list of intersection coordinates to sort
     */
    private void insertionSort(List<Double> intersections) {
        for (int i = 1; i < intersections.size(); i++) {
            double key = intersections.get(i);
            int j = i - 1;

            while (j >= 0 && intersections.get(j) > key) {
                intersections.set(j + 1, intersections.get(j));
                j = j - 1;
            }
            intersections.set(j + 1, key);
        }
    }

    private Polygon getPolygonByMouseCoordinates(Solid solid, int mouseX, int mouseY) {
        double minDistSq = Double.MAX_VALUE;
        Polygon nearestPolygon = null;

        for (Polygon polygon : solid.getSolidPolygons()) {
            for (int i = 0; i < polygon.getPoints().size(); i++) {
                Point p = polygon.getPoint(i);

                double dx = p.getX() - mouseX;
                double dy = p.getY() - mouseY;
                double distSq = dx * dx + dy * dy;

                if (distSq < minDistSq) {
                    minDistSq = distSq;
                    nearestPolygon = polygon;
                }
            }
        }

        return nearestPolygon;
    }

    /**
     * Performs the scan-line polygon filling.
     * 
     * Steps:
     * 
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

            insertionSort(intersections);

            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                double x1 = intersections.get(i);
                double x2 = intersections.get(i + 1);

                int xLeft = (int) Math.ceil(x1);
                int xRight = (int) Math.floor(x2);

                for (int x = xLeft; x <= xRight; x++) {
                    int color = (patternRaster != null && fillColor == null) ? paint(x, y) : fillColor.getRGB();
                    raster.setPixel(x, y, color);
                }
            }
        }
    }

    /**
     * Fills the given solid using the scan-line algorithm.
     *
     * @param solid Solid to fill
     */
    @Override
    public void fill(Solid solid, int mouseX, int mouseY) {
        Polygon polygon;

        if (fillColor == null && patternRaster == null) {
            System.out.println("Color or pattern for filling is required");
            return;
        }

        polygon = getPolygonByMouseCoordinates(solid, mouseX, mouseY);

        fill(polygon);
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

        if (polygon == null || polygon.getPoints().size() < 3) {
            System.out.println("Closed Polygon is required");
            return;
        }

        scanLine(polygon);
    }
}
