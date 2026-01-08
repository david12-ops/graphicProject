package com.example.rasterize;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.example.enums.ColorMode;
import com.example.raster.Raster;
import com.example.raster.RasterBufferedImage;

public class LineRasterizerGraphics extends LineRasterizer {

    /**
     * Creates a rasterizer algorithm instance.
     * 
     * 
     * @param raster    Raster where the line drawing algorithm will be performed
     * @param colorMode Decides which color will be used for drawing line
     */
    public LineRasterizerGraphics(Raster raster, ColorMode colorMode) {
        super(raster, colorMode);
    }

    /**
     * Rasterizes a line using Java2D drawing.
     * 
     * Supports both solid color and gradient color modes.
     * When gradient mode is active, a {@link GradientPaint} is used
     * to interpolate colors between the start and end points.
     *
     * @param x1 Start x-coordinate of the line
     * @param y1 Start y-coordinate of the line
     * @param x2 End x-coordinate of the line
     * @param y2 End y-coordinate of the line
     */
    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        Graphics g = ((RasterBufferedImage) raster).getImg().getGraphics();
        Graphics2D g2;

        if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {
            g2 = (Graphics2D) g;
            GradientPaint gradientPaint = new GradientPaint(
                    x1, y1, startColor, x2, y2, endColor);

            g2.setPaint(gradientPaint);
            g2.drawLine(x1, y1, x2, y2);
        } else if (colorMode == ColorMode.SOLID && solidColor != null) {
            g.setColor(this.solidColor);
            g.drawLine(x1, y1, x2, y2);
        } else {
            System.out.println(
                    "Color mode is invalid or missing colors to draw.");
            System.out.println("Check if colors are set with color mode that use them.");
        }
    }
}
