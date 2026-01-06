package com.example.rasterize;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.model.Line;
import com.example.model.Point;
import com.example.raster.Raster;

public class LineRasterizer {

    protected Raster raster;

    protected Color color;
    protected Color startColor;
    protected Color endColor;

    protected ColorMode colorMode;
    protected RasterizerMode rasterizerMode;

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

    public List<Color> getColors() {
        List<Color> colorList = new ArrayList<>();

        if (colorMode == ColorMode.GRADIENT) {
            colorList.add(startColor);
            colorList.add(endColor);
        }

        if (colorMode == ColorMode.SOLID)
            colorList.add(color);

        return colorList;

    }

    public void rasterize(Line line) {
        rasterize(line.getPointA(), line.getPointB());
    }

    public void rasterize(Point a, Point b) {

    }
}