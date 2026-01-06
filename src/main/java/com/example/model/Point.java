package com.example.model;

import java.awt.Color;

import com.example.raster.Raster;

public class Point {

    private int x, y;
    private int color;
    private int size;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color.getRGB();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void resizePoint(Raster raster) {
        // namalovani ctverce (point) na zacatku a konci usecky
        // aby byl videt - pouzito centrovani bodu -> -velikost/2 do +velikost/2

        if (size == 0 || color == 0)
            return;

        for (int dx = -size / 2; dx <= size / 2; dx++) {
            for (int dy = -size / 2; dy <= size / 2; dy++) {
                // souradnice pixelku - bere se ten co uz je + offset (dx,dy) pro videlost bodu
                int px = this.getX() + dx;
                int py = this.getY() + dy;

                raster.setPixel(px, py, color);
            }
        }
    }

}
