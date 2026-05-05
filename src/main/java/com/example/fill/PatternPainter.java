package com.example.fill;

import com.example.raster.Raster;
import com.example.transforms.Col;

/**
 * Abstract base class for pattern-based painting.
 * 
 * Provides access to a pattern raster and a helper method
 * for retrieving repeating pattern colors.
 */
public abstract class PatternPainter {

    protected final Raster<Col> patternRaster;

    /**
     * Creates a pattern painter with the given pattern raster.
     *
     * @param patternRaster Raster defining the fill pattern
     */
    protected PatternPainter(Raster<Col> patternRaster) {
        this.patternRaster = patternRaster;
    }

    /**
     * Returns the pattern color for the given coordinates.
     * 
     * Coordinates are wrapped using modulo operation
     * to create a repeating pattern.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return Color from the pattern raster at wrapped coordinates
     */
    protected Col paint(int x, int y) {
        int i = Math.floorMod(x, patternRaster.getWidth());
        int j = Math.floorMod(y, patternRaster.getHeight());

        return patternRaster.getValue(x, y).orElse(new Col(0, 0, 0));
    }
}
