package com.example.rasterize;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.example.enums.ColorMode;
import com.example.raster.Raster;
import com.example.raster.RasterBufferedImage;

public class LineRasterizerGraphics extends LineRasterizer {

    public LineRasterizerGraphics(Raster raster, ColorMode colorMode) {
        super(raster, colorMode);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        Graphics g = ((RasterBufferedImage) raster).getImage().getGraphics();
        Graphics2D g2;

        if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {
            g2 = (Graphics2D) g;
            GradientPaint gradientPaint = new GradientPaint(
                    x1, y1, startColor, x2, y2, endColor);

            g2.setPaint(gradientPaint);
            g2.drawLine(x1, y1, x2, y2);
        } else if (colorMode == ColorMode.SOLID && color != null) {
            g.setColor(this.color);
            g.drawLine(x1, y1, x2, y2);
        } else {
            System.out.println("Color mode is invalid or missing colors to draw");
        }
    }
}
