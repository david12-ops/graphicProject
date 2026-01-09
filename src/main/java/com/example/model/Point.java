package com.example.model;

import java.awt.Color;

import com.example.raster.Raster;

public class Point {

    private int x, y;
    private int color;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(Color color) {
        this.color = color.getRGB();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Draws a visible square representation of the point.
     * 
     * The point is centered at its coordinates and rendered as a square
     * of the given size to improve visibility.
     *
     * @param size   Size of the square in pixels
     * @param raster Raster where the point will be drawn
     */
    public void resizePoint(int size, Raster raster) {

        // Draw a centered square around the point
        for (int dx = -size / 2; dx <= size / 2; dx++) {
            for (int dy = -size / 2; dy <= size / 2; dy++) {
                int px = this.getX() + dx;
                int py = this.getY() + dy;

                raster.setPixel(px, py, color);
            }
        }
    }
}
