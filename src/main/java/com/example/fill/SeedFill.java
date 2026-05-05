package com.example.fill;

import java.util.Optional;
import java.util.Stack;

import com.example.model.Point;
import com.example.raster.Raster;
import com.example.transforms.Col;

/**
 * Implements an iterative seed fill (flood fill) algorithm.
 * 
 * Supports both solid color filling and pattern-based filling.
 * Uses an explicit stack to avoid recursion and stack overflow.
 */
public class SeedFill extends PatternPainter implements SeedFiller {
    private Raster<Col> raster;
    private int x, y;
    private Col fillColor;
    private Col backgroundColor;

    /**
     * Creates a seed fill using a solid fill color.
     *
     * @param raster          Target raster to be filled
     * @param backgroundColor Color that will be replaced
     * @param fillColor       Fill color (RGB)
     * @param x               Starting x-coordinate
     * @param y               Starting y-coordinate
     */
    public SeedFill(Raster<Col> raster, Col backgroundColor, Col fillColor, int x, int y) {
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
    public SeedFill(Raster<Col> raster, Raster<Col> patternRaster, Col backgroundColor, int x, int y) {
        super(patternRaster);
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.fillColor = null;
        this.x = x;
        this.y = y;
    }

    private int normalize(int argb) {
        return argb | 0xFF000000; // force alpha = 255
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

            Optional<Col> pixel = raster.getValue(p.getX(), p.getY());

            if (pixel.orElse(backgroundColor) != backgroundColor)
                continue;

            Col color;
            if (patternRaster != null) {
                color = paint(p.getX(), p.getY());
            } else if (fillColor != null) {
                color = fillColor;
            } else {
                continue; // or throw IllegalStateException
            }

            raster.setValue(p.getX(), p.getY(), color);

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

        if (fillColor == null && patternRaster == null) {
            System.out.println("Color or pattern for filling is required");
            return;
        }

        if (fillColor != null &&
                patternRaster == null
                && backgroundColor == fillColor) {
            System.out.println("Fill color equals background color");
            return;
        }

        seedFill(x, y);
    }
}
