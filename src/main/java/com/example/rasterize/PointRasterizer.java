package com.example.rasterize;

import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;

/**
 * Rasterizer responsible for rendering point primitives.
 *
 * <p>
 * A point is rasterized as a single pixel in screen space.
 * The rasterizer performs:
 * </p>
 *
 * <ol>
 * <li>Depth testing using the Z-buffer</li>
 * <li>Fragment shading</li>
 * <li>Framebuffer update</li>
 * </ol>
 *
 * <p>
 * The vertex is assumed to already be transformed into screen coordinates.
 * </p>
 */
public class PointRasterizer {

    private ZBuffer zBuffer;

    /**
     * Creates a point rasterizer.
     *
     * @param zBuffer depth buffer used for depth testing
     */
    public PointRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    /**
     * Rasterizes a single point primitive.
     *
     * <p>
     * The point position is interpreted directly in screen space.
     * The fragment color is computed by the provided shader and
     * written to the framebuffer only if the depth test passes.
     * </p>
     *
     * @param vertex point vertex in screen space
     * @param shader fragment shader used for color computation
     */
    public void rasterize(Vertex vertex, Shader shader) {
        zBuffer.setPixelWithZTest(
                (int) vertex.getX(),
                (int) vertex.getY(),
                vertex.getZ(),
                shader.getColor(vertex));
    }
}
