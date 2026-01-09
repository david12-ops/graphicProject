package com.example.fill;

import java.util.Stack;

import com.example.model.Point;
import com.example.raster.Raster;

/**
 * Implements an iterative seed fill (flood fill) algorithm.
 * 
 * Supports both solid color filling and pattern-based filling.
 * Uses an explicit stack to avoid recursion and stack overflow.
 */
public class SeedFill extends PatternPainter implements SeedFiller {
    private Raster raster;
    private int x, y;
    private int backgroundColor, fillColor;

    /**
     * Creates a seed fill using a solid fill color.
     *
     * @param raster          Target raster to be filled
     * @param backgroundColor Color that will be replaced
     * @param fillColor       Fill color (RGB)
     * @param x               Starting x-coordinate
     * @param y               Starting y-coordinate
     */
    public SeedFill(Raster raster, int backgroundColor, int fillColor, int x, int y) {
        super(null);
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.fillColor = fillColor;
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a seed fill using a repeating pattern.
     *
     * @param raster          Target raster to be filled
     * @param patternRaster   Raster defining the fill pattern
     * @param backgroundColor Color that will be replaced
     * @param x               Starting x-coordinate
     * @param y               Starting y-coordinate
     */
    public SeedFill(Raster raster, Raster patternRaster, int backgroundColor, int x, int y) {
        super(patternRaster);
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.fillColor = -1;
        this.x = x;
        this.y = y;
    }

    /**
     * Performs the iterative seed fill using a stack.
     * 
     * Neighboring pixels are processed in four directions
     * (left, right, up, down).
     *
     * @param x Starting x-coordinate
     * @param y Starting y-coordinate
     */
    private void seedFill(int x, int y) {
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));

        while (!stack.empty()) {
            Point p = stack.pop();

            // Bounds check
            if (p.getX() < 0 || p.getY() < 0 || p.getX() >= raster.getWidth() || p.getY() >= raster.getHeight())
                continue;

            int pixel = raster.getPixel(p.getX(), p.getY());

            // Skip invalid pixels or pixels that are already filled
            if (pixel == -1)
                continue;

            if (pixel != backgroundColor)
                continue;

            int color = (patternRaster != null && fillColor == -1) ? paint(p.getX(), p.getY()) : fillColor;

            raster.setPixel(p.getX(), p.getY(), color);

            stack.push(new Point(p.getX() + 1, p.getY()));
            stack.push(new Point(p.getX() - 1, p.getY()));
            stack.push(new Point(p.getX(), p.getY() + 1));
            stack.push(new Point(p.getX(), p.getY() - 1));
        }
    }

    /**
     * Starts the seed fill operation.
     * 
     * If the fill color is the same as the background color,
     * the operation is skipped.
     */
    @Override
    public void fill() {
        if (patternRaster == null && backgroundColor == fillColor)
            return;

        seedFill(x, y);
    }
}
