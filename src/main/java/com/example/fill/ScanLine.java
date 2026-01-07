package com.example.fill;

import java.util.ArrayList;
import java.util.List;

import com.example.model.Line;
import com.example.model.Point;
import com.example.model.Polygon;
import com.example.raster.Raster;

public class ScanLine extends PatternPainter implements PolygonFiller {

    private Raster raster;

    private List<Line> lines = new ArrayList<>();
    private List<Line> tempLines = new ArrayList<>();
    private List<Integer> intersections = new ArrayList<>();
    int ymin = Integer.MAX_VALUE, ymax = Integer.MIN_VALUE;
    private int fillColor;

    public ScanLine(Raster raster, int fillColor) {
        super(null);
        this.raster = raster;
        this.fillColor = fillColor;
    }

    public ScanLine(Raster raster, Raster patternRaster) {
        super(patternRaster);
        this.raster = raster;
        this.fillColor = -1;
    }

    private void scanLine(Polygon polygon) {

        for (int i = 0; i < polygon.getSize(); i++) {
            Point a = polygon.getPoint(i);
            Point b = polygon.getPoint((i + 1) % polygon.getSize());

            if (a.getY() == b.getY())
                continue;

            Line e = new Line(a, b);
            e.normalize();
            e.compute();
            e.shorten();

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

            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                int x1 = intersections.get(i);
                int x2 = intersections.get(i + 1);

                for (int x = x1; x <= x2; x++) {
                    int color = (patternRaster != null && fillColor == -1) ? paint(x, y) : fillColor;

                    raster.setPixel(x, y, color);
                }
            }
        }
    }

    @Override
    public void fill(Polygon polygon) {
        scanLine(polygon);
    }
}
