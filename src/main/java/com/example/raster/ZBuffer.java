package com.example.raster;

import com.example.transforms.Col;

/**
 * Z-buffer implementation used for hidden surface removal.
 *
 * <p>
 * The Z-buffer stores:
 * </p>
 *
 * <ul>
 * <li>A color framebuffer</li>
 * <li>A depth buffer containing the nearest depth value per pixel</li>
 * </ul>
 *
 * <p>
 * During rasterization, each fragment is depth-tested before being written
 * into the framebuffer.
 * </p>
 *
 * <p>
 * A fragment passes the depth test if:
 * </p>
 *
 * :contentReference[oaicite:0]{index=0}
 *
 * <p>
 * If the test succeeds:
 * </p>
 *
 * <ol>
 * <li>The depth buffer is updated</li>
 * <li>The color framebuffer is updated</li>
 * </ol>
 *
 * <p>
 * The depth buffer is initialized with positive infinity, meaning that
 * initially no geometry has been rendered.
 * </p>
 */
public class ZBuffer {
    private final Raster<Col> imageBuffer;
    private final Raster<Double> depthBuffer;

    /**
     * Creates a Z-buffer for the specified image buffer.
     *
     * <p>
     * The depth buffer dimensions match the framebuffer dimensions.
     * </p>
     *
     * @param imageBuffer color framebuffer
     */
    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    /**
     * Performs depth testing and conditionally writes a pixel.
     *
     * <p>
     * The fragment is written only if its depth value is smaller
     * than the currently stored depth value.
     * </p>
     *
     * @param x     pixel x coordinate
     * @param y     pixel y coordinate
     * @param z     fragment depth
     * @param color fragment color
     */
    public void setPixelWithZTest(int x, int y, double z, Col color) {
        if (z < depthBuffer.getValue(x, y).orElse(Double.POSITIVE_INFINITY)) {
            depthBuffer.setValue(x, y, z);
            imageBuffer.setValue(x, y, color);
        }
    }

    public int getHeight() {
        return depthBuffer.getHeight();
    }

    public int getWidth() {
        return depthBuffer.getWidth();
    }

    /**
     * Clears the depth buffer.
     *
     * <p>
     * All depth values are reset to positive infinity.
     * </p>
     */
    public void clear() {
        depthBuffer.clear();
    }
}
