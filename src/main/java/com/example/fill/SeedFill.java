package com.example.fill;

import com.example.raster.Raster;

public class SeedFill implements Filler {
    private Raster raster;
    private int x, y;
    private int backgroundColor;

    /**
     * Creates a seed fill (flood fill) algorithm instance.
     * Optimalization with Stack in task2
     * 
     * @param raster          Raster where the fill will be performed
     * @param backgroundColor Color that will be replaced by the fill
     * @param x               Starting x-coordinate of the fill
     * @param y               Starting y-coordinate of the fill
     */
    public SeedFill(Raster raster, int backgroundColor, int x, int y) {
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.x = x;
        this.y = y;
    }

    /**
     * Starts the seed fill algorithm from the initial coordinates.
     */
    @Override
    public void fill() {
        seedFill(x, y);
    }

    /**
     * Recursively fills neighboring pixels using the seed fill algorithm.
     * 
     * The method stops when:
     * The pixel is outside the raster
     * The pixel color does not match the background color
     *
     * @param x Current x-coordinate
     * @param y Current y-coordinate
     */
    private void seedFill(int x, int y) {
        int pixelColor = raster.getPixel(x, y);
        if (pixelColor == -1 || pixelColor != backgroundColor)
            return;

        raster.setPixel(x, y, 0x0000ff);

        seedFill(x + 1, y);
        seedFill(x - 1, y);
        seedFill(x, y + 1);
        seedFill(x, y - 1);
    }
}
