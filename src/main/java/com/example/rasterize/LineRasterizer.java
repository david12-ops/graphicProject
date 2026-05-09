package com.example.rasterize;

import java.util.ArrayList;
import java.util.List;

import com.example.enums.ColorMode;
import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;
import com.example.transforms.Col;

public class LineRasterizer {

    protected ZBuffer zBuffer;

    protected Col solidColor;
    protected Col startColor;
    protected Col endColor;

    protected ColorMode colorMode;

    public LineRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
        this.solidColor = null;
        this.startColor = null;
        this.endColor = null;
    }

    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
    }

    // solid color setters
    public void setSolidColor(Col solidColor) {
        this.solidColor = solidColor;
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = new Col(solidColor);
    }

    // colors for gradient setters
    public void setGradientColors(Col startColor, Col endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public void setGradientColors(int startColor, int endColor) {
        this.startColor = new Col(startColor);
        this.endColor = new Col(endColor);
    }

    public ColorMode getColorMode() {
        return this.colorMode;
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
    public List<Col> getColors() {
        List<Col> colorList = new ArrayList<>();

        if (colorMode == ColorMode.GRADIENT) {
            colorList.add(startColor);
            colorList.add(endColor);
        }

        if (colorMode == ColorMode.SOLID) {
            colorList.add(solidColor);
        }

        return colorList;
    }

    public void rasterize(Vertex a, double invW1, double zOverW1, Vertex b, double invW2, double zOverW2,
            Shader shader) {

    }

    public void rasterize(Vertex vertex, Shader shader) {

    }
}