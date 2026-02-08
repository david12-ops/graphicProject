package com.example.rasterize;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.example.enums.ColorMode;
import com.example.model.Point;
import com.example.raster.Raster;
import com.example.raster.RasterBufferedImage;

public class LineRasterizerGraphics extends LineRasterizer {

    /**
     * Creates a rasterizer algorithm instance.
     * 
     * @param raster Raster where the line drawing algorithm will be performed
     */
    public LineRasterizerGraphics(Raster raster) {
        super(raster);
    }

    /**
     * Rasterizes a line using Java2D drawing.
     * 
     * Supports both solid color and gradient color modes.
     * When gradient mode is active, a {@link GradientPaint} is used
     * to interpolate colors between the start and end points.
     *
     * @param x1 Start point a (x - a.getX, y - a.getY)
     * @param y1 End point b (x - b.getX, y - b.getY)
     */
    @Override
    public void rasterize(Point a, Point b) {
        Graphics g = ((RasterBufferedImage) raster).getImg().getGraphics();
        Graphics2D g2;

        if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {
            g2 = (Graphics2D) g;

            if (selectedColor == null) {
                GradientPaint gradientPaint = new GradientPaint(
                        a.getX(), a.getY(), new Color(startColor.getARGB(), true), b.getX(), b.getY(),
                        new Color(endColor.getARGB(), true));

                g2.setPaint(gradientPaint);
            } else {
                g2.setColor(new Color(selectedColor.getARGB(), true));
            }

            g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
        } else if (colorMode == ColorMode.SOLID && solidColor != null) {
            g.setColor(this.selectedColor == null ? new Color(solidColor.getARGB(), true)
                    : new Color(selectedColor.getARGB(), true));
            g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
        } else {
            System.out.println(
                    "Color mode is invalid or missing colors to draw.");
            System.out.println("Check if colors are set with color mode that use them.");
        }
    }
}
