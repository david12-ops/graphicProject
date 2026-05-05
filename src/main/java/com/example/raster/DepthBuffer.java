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
        return Optional.of(zBuffer[x][y]);
    }

    @Override
    public void setValue(int x, int y, Double value) {
        zBuffer[x][y] = value;
    }
}
