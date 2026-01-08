package com.example.rasterize;

import java.awt.Color;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.model.Line;
import com.example.raster.Raster;

public class LineRasterizer {

    protected Raster raster;

    protected Color solidColor;
    protected Color startColor;
    protected Color endColor;

    protected ColorMode colorMode;
    protected RasterizerMode rasterizerMode;

    public LineRasterizer(Raster raster) {
        this.raster = raster;
        this.solidColor = null;
        this.startColor = null;
        this.endColor = null;
    }

    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
    }

    public void setSolidColor(Color solidColor) {
        this.solidColor = solidColor;
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = new Color(solidColor);
    }

    public void setRasterizeMode(RasterizerMode rasterizerMode) {
        this.rasterizerMode = rasterizerMode;
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
        return this.rasterizerMode;
    }

    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }
}