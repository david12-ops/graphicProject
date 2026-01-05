package com.example.rasterize;

import java.awt.Color;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.model.Line;
import com.example.raster.Raster;

public class LineRasterizer {

    protected Raster raster;

    protected Color color;
    protected Color startColor;
    protected Color endColor;

    protected ColorMode colorMode;
    protected RasterizerMode mode;

    public LineRasterizer(Raster raster, ColorMode colorMode) {
        this.raster = raster;
        this.colorMode = colorMode;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public void setRasterizeMode(RasterizerMode mode) {
        this.mode = mode;
    }

    public void setGradientColors(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public void setGradientColors(int startColor, int endColor) {
        this.startColor = new Color(startColor);
        this.endColor = new Color(endColor);
    }

    public ColorMode getColorMode() {
        return this.colorMode;
    }

    public RasterizerMode getRasterizerMode() {
        return this.mode;
    }

    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }
}