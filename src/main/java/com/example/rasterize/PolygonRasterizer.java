package com.example.rasterize;

import com.example.model.Point;
import com.example.model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        if (polygon.getSize() <= 2)
            return;

        for (int i = 0; i < polygon.getSize(); i++) {
            int indexA = i;
            int indexB = i + 1;

            if (indexB == polygon.getSize())
                indexB = 0;

            Point pointA = polygon.getPoint(indexA);
            Point pointB = polygon.getPoint(indexB);

            lineRasterizer.rasterize(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
        }
    }
}
