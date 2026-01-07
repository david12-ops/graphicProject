package com.example.fill;

import java.util.Stack;

import com.example.model.Point;
import com.example.raster.Raster;

public class SeedFillBorder extends PatternPainter implements SeedFiller {
    private Raster raster;
    private int x, y;
    private int borderColor, fillColor;

    public SeedFillBorder(Raster raster, int borderColor, int fillColor, int x, int y) {
        super(null);
        this.raster = raster;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.x = x;
        this.y = y;
    }

    public SeedFillBorder(Raster raster, Raster patternRaster, int borderColor, int x, int y) {
        super(patternRaster);
        this.raster = raster;
        this.borderColor = borderColor;
        this.fillColor = -1;
        this.x = x;
        this.y = y;
    }

    private void seedFill(int x, int y) {
        int startColor = raster.getPixel(x, y);

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));

        if (startColor == borderColor)
            return;

        while (!stack.empty()) {
            Point p = stack.pop();

            if (p.getX() < 0 || p.getY() < 0 || p.getX() >= raster.getWidth() || p.getY() >= raster.getHeight())
                continue;

            int pixel = raster.getPixel(p.getX(), p.getY());

            if (pixel == -1)
                continue;

            if (pixel == borderColor)
                continue;

            if (pixel != startColor)
                continue;

            int color = (patternRaster != null && fillColor == -1) ? paint(p.getX(), p.getY()) : fillColor;

            raster.setPixel(p.getX(), p.getY(), color);

            stack.push(new Point(p.getX() + 1, p.getY()));
            stack.push(new Point(p.getX() - 1, p.getY()));
            stack.push(new Point(p.getX(), p.getY() + 1));
            stack.push(new Point(p.getX(), p.getY() - 1));
        }
    }

    @Override
    public void fill() {
        seedFill(x, y);
    }
}
