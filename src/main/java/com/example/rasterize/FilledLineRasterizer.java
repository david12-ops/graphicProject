package com.example.rasterize;

import java.awt.Color;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.model.Line;
import com.example.model.Point;
import com.example.raster.Raster;

/* 
Nevýhoda: je to funkce - „jednomu x odpovídá právě jedno y“ 
jak řešit svislou úsečku?
násobení a sčítání v plovoucí řádové čárce
neefektivní!
Výhoda: postup použitelný i pro složitější křivky
Poznatek : nutné rešení vertikální úsečky (formule (y - q) / k -> k != 0 -> x2 != x1)
*/

public class FilledLineRasterizer extends LineRasterizer {

    public FilledLineRasterizer(Raster raster, ColorMode colorMode) {
        super(raster, colorMode);
    }

    @Override
    public void rasterize(Point a, Point b) {
        if (rasterizerMode == RasterizerMode.NORMAL)
            trivialAlgorithm(a.getX(), a.getY(), b.getX(), b.getY());
        else if (rasterizerMode == RasterizerMode.SHIFT) {
            Point endPoint = snapToHVOrDiagonal(a, b);
            trivialAlgorithm(a.getX(), a.getY(), endPoint.getX(), endPoint.getY());
        } else
            return;
    }

    @Override
    public void rasterize(Line line) {
        rasterize(line.getPointA(), line.getPointB());
    }

    private void trivialAlgorithm(int x1, int y1, int x2, int y2) {
        Point point1;
        Point point2;
        // y = kx + q
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        if (Math.abs((y2 - y1)) < Math.abs(x2 - x1)) {

            if (x2 < x1) {

                int t;
                t = x1;
                x1 = x2;
                x2 = t;

                t = y1;
                y1 = y2;
                y2 = t;
            }

            // Clipping - instead of checking if to goes out, i will clip the for cycle to
            // only valid bounds
            int startX = Math.max(0, x1);
            int endX = Math.min(raster.getWidth() - 1, x2);

            point1 = new Point(x1, y1);
            point2 = new Point(x2, y2);

            if (colorMode == ColorMode.SOLID && color != null) {

                setColorAndSizeToPoint(6, color.getRGB(), point1);

                for (int x = startX; x < endX; x++) {
                    int y = Math.round(k * x + q);

                    // skip drawing when y is outside raster
                    if (y < 0 || y >= raster.getHeight()) {
                        continue;
                    }

                    raster.setPixel(x, y, color.getRGB());
                }

                setColorAndSizeToPoint(6, color.getRGB(), point2);
            } else if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {

                setColorAndSizeToPoint(6, startColor.getRGB(), point1);

                for (int x = startX; x < endX; x++) {
                    int y = Math.round(k * x + q);
                    float w = (x - x1) / (float) (x2 - x1);

                    // skip drawing when y is outside raster
                    if (y < 0 || y >= raster.getHeight()) {
                        continue;
                    }

                    raster.setPixel(x, y, computeColor(w, startColor, endColor));
                }

                setColorAndSizeToPoint(6, endColor.getRGB(), point2);
            } else {
                System.out.println("Color mode is invalid or missing colors to draw");
            }

        } else {

            if (y2 < y1) {
                int t;
                t = x1;
                x1 = x2;
                x2 = t;
                t = y1;
                y1 = y2;
                y2 = t;
            }

            // Clipping - instead of checking if to goes out, i will clip the for cycle to
            // only valid bounds
            int startY = Math.max(0, y1);
            int endY = Math.min(raster.getHeight() - 1, y2);

            point1 = new Point(x1, y1);
            point2 = new Point(x2, y2);

            if (Float.isInfinite(k)) {
                int x = x1;
                if (colorMode == ColorMode.SOLID && color != null) {

                    setColorAndSizeToPoint(6, color.getRGB(), point1);

                    for (int y = startY; y < endY; y++) {
                        if (x >= 0 && x < raster.getWidth())
                            raster.setPixel(x, y, color.getRGB());

                    }

                    setColorAndSizeToPoint(6, color.getRGB(), point2);
                    return;
                } else if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {

                    setColorAndSizeToPoint(6, startColor.getRGB(), point1);

                    for (int y = startY; y < endY; y++) {
                        float w = (y - y1) / (float) (y2 - y1);

                        if (x >= 0 && x < raster.getWidth())
                            raster.setPixel(x, y, computeColor(w, startColor, endColor));

                    }

                    setColorAndSizeToPoint(6, endColor.getRGB(), point2);
                    return;
                } else {
                    System.out.println("Color mode is invalid or missing colors to draw");
                }
            }

            if (colorMode == ColorMode.SOLID && color != null) {

                setColorAndSizeToPoint(6, color.getRGB(), point1);

                for (int y = startY; y < endY; y++) {
                    int x = Math.round((y - q) / k);

                    // skip drawing when x is outside raster
                    if (x < 0 || x >= raster.getWidth()) {
                        continue;
                    }

                    raster.setPixel(x, y, color.getRGB());
                }

                setColorAndSizeToPoint(6, color.getRGB(), point2);
            } else if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {

                setColorAndSizeToPoint(6, startColor.getRGB(), point1);

                for (int y = startY; y < endY; y++) {
                    int x = Math.round((y - q) / k);
                    float w = (y - y1) / (float) (y2 - y1);

                    // skip drawing when x is outside raster
                    if (x < 0 || x >= raster.getWidth()) {
                        continue;
                    }

                    raster.setPixel(x, y, computeColor(w, startColor, endColor));
                }

                setColorAndSizeToPoint(6, endColor.getRGB(), point2);
            } else {
                System.out.println("Color mode is invalid or missing colors to draw");
            }
        }
    }

    private Point snapToHVOrDiagonal(Point a, Point b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();

        int adx = Math.abs(dx);
        int ady = Math.abs(dy);

        if (adx > 2 * ady) {
            return new Point(b.getX(), a.getY());
        }

        if (ady > 2 * adx) {
            return new Point(a.getX(), b.getY());
        }

        int d = Math.min(adx, ady);
        return new Point(
                a.getX() + Integer.signum(dx) * d,
                a.getY() + Integer.signum(dy) * d);
    }

    private int computeColor(float w, Color startColor, Color endColor) {
        if (w < 0f)
            w = 0f;
        else if (w > 1f)
            w = 1f;

        // For each channel (R, G, B):
        int r = Math.round(startColor.getRed() * (1 - w) + endColor.getRed() * w);
        int g = Math.round(startColor.getGreen() * (1 - w) + endColor.getGreen() * w);
        int b = Math.round(startColor.getBlue() * (1 - w) + endColor.getBlue() * w);

        // revert back
        return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xFF);
    }

    private void setColorAndSizeToPoint(int newSize, int newColor, Point point) {
        point.setColor(newColor);
        point.setSize(newSize);
        point.resizePoint(raster);
    }
}
