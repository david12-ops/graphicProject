package com.example.rasterize;

import java.awt.Color;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.model.Line;
import com.example.model.Point;
import com.example.raster.Raster;

/*
 * Disadvantages:
 * - This is a function where one x corresponds to exactly one y
 * - Requires special handling of vertical lines
 * - Uses floating-point arithmetic (multiplication and addition)
 * - Inefficient
 *
 * Advantages:
 * - The approach can be extended to more complex curves
 *
 * Note:
 * - Vertical line handling is required
 * - Formula (y - q) / k is valid only if k != 0 (i.e., x2 != x1)
 */

/**
 * Line rasterizer using a simple (trivial) line drawing algorithm.
 * 
 * Supports solid and gradient coloring, endpoint visualization,
 * and snapping to horizontal, vertical, or diagonal directions.
 */
public class FilledLineRasterizer extends LineRasterizer {

    /**
     * Creates a rasterizer algorithm instance.
     * 
     * @param raster    Raster where the line drawing algorithm will be performed
     * @param colorMode Decides which color will be used for drawing line
     */
    public FilledLineRasterizer(Raster raster, ColorMode colorMode) {
        super(raster, colorMode);
    }

    /**
     * Rasterizes a line defined by two points.
     * 
     * If SHIFT mode is active, the line endpoint is snapped
     * to horizontal, vertical, or diagonal direction.
     *
     * @param x1 Start x-coordinate
     * @param y1 Start y-coordinate
     * @param x2 End x-coordinate
     * @param y2 End y-coordinate
     */
    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        if (rasterizerMode == RasterizerMode.NORMAL)
            trivialAlgorithm(x1, y1, x2, y2);
        else if (rasterizerMode == RasterizerMode.SHIFT) {
            Point endPoint = snapToHVOrDiagonal(new Point(x1, y1), new Point(x2, y2));
            trivialAlgorithm(x1, y1, endPoint.getX(), endPoint.getY());
        } else
            return;
    }

    /**
     * Rasterizes a line model object.
     *
     * @param line Line to rasterize
     */
    @Override
    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    /**
     * Draws a line using a simple analytical (trivial) algorithm.
     *
     * Chooses the dominant axis (X or Y) to avoid gaps
     * and supports both solid and gradient coloring.
     * Implemented clip the for cycle to only valid bounds
     * {@code -  Math.max(0, x1); Math.min(raster.getWidth() - 1, x2); - for X}
     * {@code -  Math.max(0, y1); Math.min(raster.getHeight() - 1, y2); - for Y}
     * 
     * @param x1 Start x-coordinate
     * @param y1 Start y-coordinate
     * @param x2 End x-coordinate
     * @param y2 End y-coordinate
     */
    private void trivialAlgorithm(int x1, int y1, int x2, int y2) {
        if (!(isGradientColorSet() || isSolidColorSet())) {
            System.out.println(
                    "Color mode is invalid or missing colors to draw.");
            System.out.println("Check if colors are set with color mode that use them.");
            return;
        }

        Point point1;
        Point point2;
        // y = kx + q
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        // X-dominant line
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

            int startX = Math.max(0, x1);
            int endX = Math.min(raster.getWidth() - 1, x2);

            point1 = new Point(x1, y1);
            point2 = new Point(x2, y2);

            if (isSolidColorSet()) {

                point1.resizePoint(6, raster);

                for (int x = startX; x < endX; x++) {
                    int y = Math.round(k * x + q);

                    // skip drawing when y is outside raster
                    if (y < 0 || y >= raster.getHeight()) {
                        continue;
                    }

                    raster.setPixel(x, y, solidColor.getRGB());
                }

                point2.resizePoint(6, raster);
            } else {

                point1.resizePoint(6, raster);

                for (int x = startX; x < endX; x++) {
                    int y = Math.round(k * x + q);
                    float w = (x - x1) / (float) (x2 - x1);

                    // skip drawing when y is outside raster
                    if (y < 0 || y >= raster.getHeight()) {
                        continue;
                    }

                    raster.setPixel(x, y, computeColor(w, startColor, endColor));
                }

                point2.resizePoint(6, raster);
            }

        } else {
            // Y-dominant line
            if (y2 < y1) {
                int t;
                t = x1;
                x1 = x2;
                x2 = t;
                t = y1;
                y1 = y2;
                y2 = t;
            }

            int startY = Math.max(0, y1);
            int endY = Math.min(raster.getHeight() - 1, y2);
            boolean isInfiniteK = false;
            int x = 0;

            point1 = new Point(x1, y1);
            point2 = new Point(x2, y2);

            if (Float.isInfinite(k)) {
                x = x1;
                isInfiniteK = true;
            }

            if (isSolidColorSet()) {

                point1.resizePoint(6, raster);

                for (int y = startY; y < endY; y++) {
                    if (!isInfiniteK) {
                        x = Math.round((y - q) / k);
                    }

                    // skip drawing when x is outside raster
                    if (x < 0 || x >= raster.getWidth()) {
                        continue;
                    }

                    raster.setPixel(x, y, solidColor.getRGB());
                }

                point2.resizePoint(6, raster);
            } else {

                point1.resizePoint(6, raster);

                for (int y = startY; y < endY; y++) {
                    if (!isInfiniteK) {
                        x = Math.round((y - q) / k);
                    }

                    float w = (y - y1) / (float) (y2 - y1);

                    // skip drawing when x is outside raster
                    if (x < 0 || x >= raster.getWidth()) {
                        continue;
                    }

                    raster.setPixel(x, y, computeColor(w, startColor, endColor));
                }

                point2.resizePoint(6, raster);
            }
        }
    }

    /**
     * Snaps the second point to horizontal, vertical,
     * or diagonal direction relative to the first point.
     *
     * @param a Start point
     * @param b Original end point
     * @return Snapped end point
     */
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

    /**
     * Computes interpolated color between two colors.
     *
     * @param w          Interpolation factor in range ⟨0,1⟩
     * @param startColor Starting color
     * @param endColor   Ending color
     * @return Interpolated RGB color
     */
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

    /**
     * Checks whether gradient color mode is correctly configured.
     */
    private boolean isGradientColorSet() {
        return colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null);
    }

    /**
     * Checks whether solid color mode is correctly configured.
     */
    private boolean isSolidColorSet() {
        return colorMode == ColorMode.SOLID && solidColor != null;
    }
}
