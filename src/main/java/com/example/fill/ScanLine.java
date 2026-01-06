package com.example.fill;

import java.util.ArrayList;
import java.util.List;

import com.example.model.Line;
import com.example.model.Point;
import com.example.model.Polygon;
import com.example.raster.Raster;

public class ScanLine implements Filler, PatternFill {

    private Raster raster;

    List<Line> lines = new ArrayList<>();
    List<Line> tempLines = new ArrayList<>();
    List<Integer> intersections = new ArrayList<>();
    int ymin = Integer.MAX_VALUE, ymax = Integer.MIN_VALUE;
    private int fillColor;

    public ScanLine(Raster raster, int fillColor) {
        this.raster = raster;
        this.fillColor = fillColor;
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
                    raster.setPixel(x, y, fillColor);
                }
            }
        }
    }

    @Override
    public void fill() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fill'");
    }

    @Override
    public void fill(Polygon polygon) {
        scanLine(polygon);
    }

    @Override
    public int paint(int x, int y) {
        return 0;
    }
}
