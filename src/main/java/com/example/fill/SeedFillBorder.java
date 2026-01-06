package com.example.fill;

import java.util.Stack;

import com.example.model.Point;
import com.example.model.Polygon;
import com.example.raster.Raster;

public class SeedFillBorder implements Filler, PatternFill {
    private Raster raster;
    private int x, y;
    private int borderColor, fillColor;

    public SeedFillBorder(Raster raster, int borderColor, int fillColor, int x, int y) {
        this.raster = raster;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.x = x;
        this.y = y;
    }

    private void seedFill(int x, int y) {
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));

        while (!stack.empty()) {
            Point p = stack.pop();

            if (p.getX() < 0 || p.getY() < 0 || p.getX() >= raster.getWidth() || p.getY() >= raster.getHeight())
                continue;

            int pixel = raster.getPixel(p.getX(), p.getY());

            if (pixel == -1)
                continue;

            if (pixel == borderColor || pixel == fillColor)
                continue;

            raster.setPixel(p.getX(), p.getY(), fillColor);

            stack.push(new Point(p.getX() + 1, p.getY()));
            stack.push(new Point(p.getX() - 1, p.getY()));
            stack.push(new Point(p.getX(), p.getY() + 1));
            stack.push(new Point(p.getX(), p.getY() - 1));
        }
    }

    @Override
    public void fill() {
        if (fillColor == borderColor)
            return;

        seedFill(x, y);
    }

    @Override
    public void fill(Polygon polygon) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fill'");
    }

    @Override
    public int paint(int x, int y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'paint'");
    }

}
