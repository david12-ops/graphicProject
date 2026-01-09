package com.example.fill;

import java.awt.Color;
import java.util.Stack;

import com.example.model.Point;
import com.example.raster.Raster;

/**
 * Implements a border-based seed fill algorithm.
 * 
 * Filling continues until a border color is reached.
 * Supports both solid color fill and pattern-based fill.
 * Uses an iterative approach to avoid recursion overflow.
 */
public class SeedFillBorder extends PatternPainter implements SeedFiller {
    private Raster raster;
    private int x, y;
    private Color borderColor, fillColor;

    /**
     * Creates a border-based seed fill using a solid fill color.
     *
     * @param raster      Target raster to be filled
     * @param borderColor Color defining the boundary of the fill area
     * @param fillColor   Fill color (RGB)
     * @param x           Starting x-coordinate
     * @param y           Starting y-coordinate
     */
    public SeedFillBorder(Raster raster, int borderColor, int fillColor, int x, int y) {
        super(null);
        this.raster = raster;
        this.fillColor = new Color(fillColor);
        this.borderColor = new Color(borderColor);
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a border-based seed fill using a repeating pattern.
     *
     * @param raster        Target raster to be filled
     * @param patternRaster Raster defining the fill pattern
     * @param borderColor   Color defining the boundary of the fill area
     * @param x             Starting x-coordinate
     * @param y             Starting y-coordinate
     */
    public SeedFillBorder(Raster raster, Raster patternRaster, int borderColor, int x, int y) {
        super(patternRaster);
        this.raster = raster;
        this.borderColor = new Color(borderColor);
        this.fillColor = null;
        this.x = x;
        this.y = y;
    }

    /**
     * Performs the border-based seed fill using an explicit stack.
     * 
     * Filling stops when the border color is encountered.
     * Neighboring pixels are processed in four directions
     * (left, right, up, down).
     *
     * @param x Starting x-coordinate
     * @param y Starting y-coordinate
     */
    private void seedFill(int x, int y) {
        int startColor = raster.getPixel(x, y);

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));

        if (startColor == borderColor.getRGB())
            return;

        while (!stack.empty()) {
            Point p = stack.pop();

            // Bounds check
            if (p.getX() < 0 || p.getY() < 0 || p.getX() >= raster.getWidth() || p.getY() >= raster.getHeight())
                continue;

            int pixel = raster.getPixel(p.getX(), p.getY());

            // Skip invalid pixels or border
            if (pixel == -1)
                continue;

            if (pixel == borderColor.getRGB())
                continue;

            // Only fill pixels matching the starting color
            if (pixel != startColor)
                continue;

            int color = (patternRaster != null && fillColor == null) ? paint(p.getX(), p.getY()) : fillColor.getRGB();

            raster.setPixel(p.getX(), p.getY(), color);

            stack.push(new Point(p.getX() + 1, p.getY()));
            stack.push(new Point(p.getX() - 1, p.getY()));
            stack.push(new Point(p.getX(), p.getY() + 1));
            stack.push(new Point(p.getX(), p.getY() - 1));
        }
    }

    /**
     * Starts the border-based seed fill operation.
     */
    @Override
    public void fill() {

        if (borderColor == null) {
            System.out.println("Border color is required");
            return;
        }

        if (fillColor == null && patternRaster == null) {
            System.out.println("Color or pattern for filling is required");
            return;
        }

        seedFill(x, y);
    }
}
