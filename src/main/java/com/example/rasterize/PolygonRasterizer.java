package com.example.rasterize;

import com.example.model.Point;
import com.example.model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    /**
     * Creates a polygon rasterizer using the given line rasterizer.
     *
     * @param lineRasterizer Line rasterizer used to draw polygon edges
     */
    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    /**
     * Rasterizes a polygon by drawing its edges.
     * 
     * The polygon is closed automatically by connecting
     * the last point to the first one.
     *
     * @param polygon Polygon to rasterize
     */
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

            if (pointA != null && pointB != null)
                lineRasterizer.rasterize(pointA, pointB);
        }
    }
}
