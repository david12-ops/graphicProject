package com.example.fill;

import com.example.raster.Raster;

public abstract class PatternPainter {

    protected final Raster patternRaster;

    protected PatternPainter(Raster patternRaster) {
        this.patternRaster = patternRaster;
    }

    protected int paint(int x, int y) {
        int i = Math.floorMod(x, patternRaster.getWidth());
        int j = Math.floorMod(y, patternRaster.getHeight());

        return patternRaster.getPixel(i, j);
    }
}
