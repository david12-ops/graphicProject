package com.example.rasterize;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.model.Line;
import com.example.model.Point;
import com.example.raster.Raster;
import com.example.transforms.Col;

public class LineRasterizer {

    protected Raster raster;

    protected Color solidColor;
    protected Color startColor;
    protected Color endColor;
    protected Color selectedColor;

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

    public void setRasterizeMode(RasterizerMode rasterizerMode) {
        this.rasterizerMode = rasterizerMode;
    }

    // solid color setters
    public void setSolidColor(Color solidColor) {
        this.solidColor = solidColor;
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = new Color(solidColor);
    }

    public void setSolidColor(Col color) {
        if (color == null)
            this.solidColor = null;
        else
            this.solidColor = new Color(color.getRGB());
    }

    // selected color setters
    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = new Color(selectedColor);
    }

    public void setSelectedColor(Col selectedColor) {
        if (selectedColor == null)
            this.selectedColor = null;
        else
            this.selectedColor = new Color(selectedColor.getRGB());
    }

    // colors for gradient setters
    public void setGradientColors(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public void setGradientColors(int startColor, int endColor) {
        this.startColor = new Color(startColor);
        this.endColor = new Color(endColor);
    }

    public void setGradientColors(Col startColor, Col endColor) {
        if (startColor != null && endColor != null) {
            this.startColor = new Color(startColor.getRGB());
            this.endColor = new Color(endColor.getRGB());
        } else {
            this.startColor = null;
            this.endColor = null;
        }
    }

    public ColorMode getColorMode() {
        return this.colorMode;
    }

    public RasterizerMode getRasterizerMode() {
        return this.rasterizerMode;
    }

    /**
     * Returns a list of colors currently used by the rasterizer.
     * 
     * If the color mode is {@link ColorMode#GRADIENT}, the list contains
     * the start and end colors in this order.
     * If the color mode is {@link ColorMode#SOLID}, the list contains
     * only the solid color.
     *
     * @return list of active colors based on the current color mode
     */
    public List<Color> getColors() {
        List<Color> colorList = new ArrayList<>();

        if (colorMode == ColorMode.GRADIENT) {
            colorList.add(startColor);
            colorList.add(endColor);
        }

        if (colorMode == ColorMode.SOLID) {
            colorList.add(solidColor);
        }

        return colorList;
    }

    public void rasterize(Line line) {
        rasterize(line.getPointA(), line.getPointB());
    }

    public void rasterize(Point a, Point b) {

    }
}