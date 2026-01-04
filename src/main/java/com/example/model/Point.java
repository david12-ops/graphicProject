package com.example.model;

import com.example.raster.Raster;

public class Point {

    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void resizePoint(int size, Raster raster) {
        // namalovani ctverce (point) na zacatku a konci usecky
        // aby byl videt - pouzito centrovani bodu -> -velikost/2 do +velikost/2
        for (int dx = -size / 2; dx <= size / 2; dx++) {
            for (int dy = -size / 2; dy <= size / 2; dy++) {
                // souradnice pixelku - bere se ten co uz je + offset (dx,dy) pro videlost bodu
                int px = this.getX() + dx;
                int py = this.getY() + dy;

                raster.setPixel(px, py, 0xFFFFFF);
            }
        }
    }

}
