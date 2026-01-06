package com.example.rasterize;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.example.enums.ColorMode;
import com.example.model.Point;
import com.example.raster.Raster;
import com.example.raster.RasterBufferedImage;

public class LineRasterizerGraphics extends LineRasterizer {

    public LineRasterizerGraphics(Raster raster, ColorMode colorMode) {
        super(raster, colorMode);
    }

    @Override
    public void rasterize(Point a, Point b) {
        Graphics g = ((RasterBufferedImage) raster).getImage().getGraphics();
        Graphics2D g2;

        if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {
            g2 = (Graphics2D) g;
            GradientPaint gradientPaint = new GradientPaint(
                    a.getX(), a.getY(), startColor, b.getX(), b.getY(), endColor);

            g2.setPaint(gradientPaint);
            g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
        } else if (colorMode == ColorMode.SOLID && color != null) {
            g.setColor(this.color);
            g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
        } else {
            System.out.println("Color mode is invalid or missing colors to draw");
        }
    }
}
