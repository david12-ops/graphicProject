package com.example.raster;

import java.util.Optional;

public class DepthBuffer implements Raster<Double> {

    private double[][] zBuffer;
    private int width, height;

    public DepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        zBuffer = new double[width][height];
    }

    /**
     * Checks whether the given pixel coordinates lie inside the raster bounds.
     *
     * <p>
     * A coordinate is considered inside if:
     * <ul>
     * <li>{@code x} is in the range {@code [0, image.getWidth())}</li>
     * <li>{@code y} is in the range {@code [0, image.getHeight())}</li>
     * </ul>
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @return {@code true} if the coordinates are inside the raster,
     *         {@code false} otherwise
     */
    private boolean isInsideRaster(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    @Override
    public void clear() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                zBuffer[x][y] = Double.POSITIVE_INFINITY;
            }
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Optional<Double> getValue(int x, int y) {
        if (!isInsideRaster(x, y))
            return Optional.empty();

        return Optional.of(zBuffer[x][y]);
    }

    @Override
    public void setValue(int x, int y, Double value) {
        if (isInsideRaster(x, y))
            zBuffer[x][y] = value;
    }
}
