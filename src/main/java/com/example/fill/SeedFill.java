package com.example.fill;

import com.example.raster.Raster;

public class SeedFill implements Filler {
    private Raster raster;
    private int x, y;
    private int fillColor, backgroundColor;

    public SeedFill(Raster raster, int backgroundColor, int x, int y) {
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.x = x;
        this.y = y;
    }

    @Override
    public void fill() {
        seedFill(x, y);
    }

    private void seedFill(int x, int y) {
        int pixelColor = raster.getPixel(x, y);
        if (pixelColor != backgroundColor)
            return;

        raster.setPixel(x, y, fillColor);

        seedFill(x + 1, y);
        seedFill(x - 1, y);
        seedFill(x, y + 1);
        seedFill(x, y - 1);
    }
}
