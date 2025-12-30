package com.example.rasterize;

import java.awt.Color;

import com.example.model.Line;
import com.example.raster.Raster;

public class LineRasterizer {

    protected Raster raster;

    protected Color color;
    protected Color startColor;
    protected Color endColor;
    protected boolean onShiftMode = false;

    public LineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public void setGradientColors(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public void setGradientColors(int startColor, int endColor) {
        this.startColor = new Color(startColor);
        this.endColor = new Color(endColor);
    }

    public void setShifMode(boolean onShifMode) {
        this.onShiftMode = onShifMode;
    }

    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }
}