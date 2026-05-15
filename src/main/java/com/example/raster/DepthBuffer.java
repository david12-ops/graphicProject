package com.example.raster;

import java.util.Optional;

/**
 * Raster storing per-pixel depth values for Z-buffering.
 *
 * <p>
 * The depth buffer is used for hidden surface removal during rasterization.
 * Each pixel stores the closest depth value rendered so far.
 * </p>
 *
 * <p>
 * Depth values are initialized to:
 * </p>
 *
 * :contentReference[oaicite:0]{index=0}
 *
 * <p>
 * meaning that initially no geometry has been drawn.
 * </p>
 *
 * <p>
 * The buffer is internally represented as a 2D array:
 * </p>
 *
 * <ul>
 * <li>X coordinate → column</li>
 * <li>Y coordinate → row</li>
 * </ul>
 */
public class DepthBuffer implements Raster<Double> {

    private double[][] zBuffer;
    private int width, height;

    /**
     * Creates a depth buffer with the specified dimensions.
     *
     * @param width  buffer width
     * @param height buffer height
     */
    public DepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        zBuffer = new double[width][height];
    }

    /**
     * Checks whether the specified coordinates lie inside the raster bounds.
     *
     * <p>
     * A coordinate is valid if:
     * </p>
     *
     * <ul>
     * <li>{@code x ∈ [0, width)}</li>
     * <li>{@code y ∈ [0, height)}</li>
     * </ul>
     *
     * @param x pixel x coordinate
     * @param y pixel y coordinate
     * @return {@code true} if coordinates are inside the raster;
     *         {@code false} otherwise
     */
    private boolean isInsideRaster(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Clears the depth buffer.
     *
     * <p>
     * All pixels are reset to positive infinity:
     * </p>
     *
     * :contentReference[oaicite:1]{index=1}
     *
     * <p>
     * This represents an empty scene where no geometry has yet been rendered.
     * </p>
     */
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
