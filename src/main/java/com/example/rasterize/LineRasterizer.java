package com.example.rasterize;

import java.awt.Color;

import com.example.model.Line;

public class LineRasterizer {

    Raster raster;
    Color color;

    public LineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void rasterize(Line line) {
        // TODO
    }

    public void rasterize(int x1, int y1, int x2, int y2, Color color) {
        // TODO
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {

    }

}
