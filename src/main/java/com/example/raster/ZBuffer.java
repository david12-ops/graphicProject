package com.example.raster;

import com.example.transforms.Col;

public class ZBuffer {
    private final Raster<Col> imageBuffer;
    private final Raster<Double> depthBuffer;

    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        if (z < depthBuffer.getValue(x, y).orElse(Double.POSITIVE_INFINITY)) {
            depthBuffer.setValue(x, y, z);
            imageBuffer.setValue(x, y, color);
        }
    }

    public int getImageBufferHeight() {
        return imageBuffer.getHeight();
    }

    public int getImageBufferWidth() {
        return imageBuffer.getWidth();
    }

    public void clear() {
        depthBuffer.clear();
        imageBuffer.clear();
    }
}
