package com.example.rasterize;

import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;
import com.example.utils.Lerp;

/*
 * Disadvantages:
 * - This is a function where one x corresponds to exactly one y
 * - Requires special handling of vertical lines
 * - Uses floating-point arithmetic (multiplication and addition)
 * - Inefficient
 *
 * Advantages:
 * - The approach can be extended to more complex curves
 *
 * Note:
 * - Vertical line handling is required
 * - Formula (y - q) / k is valid only if k != 0 (i.e., x2 != x1)
 */

/**
 * Line rasterizer using a simple analytical (trivial) line drawing algorithm.
 *
 * <p>
 * The rasterizer interpolates pixels between two screen-space vertices using
 * a linear line equation.
 * </p>
 *
 * <p>
 * Features:
 * </p>
 *
 * <ul>
 * <li>Perspective-correct depth interpolation</li>
 * <li>Depth testing through Z-buffer</li>
 * <li>Vertex attribute interpolation</li>
 * <li>Support for horizontal, vertical, and diagonal lines</li>
 * </ul>
 *
 * <p>
 * Depending on line orientation, iteration is performed over the dominant axis
 * to reduce visual gaps:
 * </p>
 *
 * <ul>
 * <li>X-major lines iterate over X</li>
 * <li>Y-major lines iterate over Y</li>
 * </ul>
 *
 * <p>
 * Line equation:
 * </p>
 *
 * 
 * ::contentReference[oaicite:0]{index=0}
 *
 * 
 * <p>
 * Perspective-correct depth interpolation:
 * </p>
 *
 * :contentReference[oaicite:1]{index=1}
 *
 * <p>
 * Advantages:
 * </p>
 *
 * <ul>
 * <li>Simple implementation</li>
 * <li>Easily extendable to curves</li>
 * </ul>
 *
 * <p>
 * Disadvantages:
 * </p>
 *
 * <ul>
 * <li>Uses floating-point arithmetic</li>
 * <li>Requires special handling of vertical lines</li>
 * <li>Less efficient than Bresenham-style algorithms</li>
 * </ul>
 */
public class FilledLineRasterizer extends LineRasterizer {

    /**
     * Creates a rasterizer algorithm instance.
     * 
     * @param zBuffer ZBuffer for depth testing
     */
    public FilledLineRasterizer(ZBuffer zBuffer) {
        super(zBuffer);
    }

    /**
     * Rasterizes a line segment between two vertices.
     *
     * <p>
     * The line is rendered in screen space using the analytical line equation.
     * Perspective-correct depth interpolation is used for Z-buffer testing.
     * </p>
     *
     * @param a       first line vertex
     * @param invW1   reciprocal clip-space W for first vertex
     * @param zOverW1 depth divided by W for first vertex
     * @param b       second line vertex
     * @param invW2   reciprocal clip-space W for second vertex
     * @param zOverW2 depth divided by W for second vertex
     * @param shader  fragment shader used for color computation
     */
    @Override
    public void rasterize(Vertex a, double invW1, double zOverW1, Vertex b, double invW2, double zOverW2,
            Shader shader) {
        trivialAlgorithm(a, invW1, zOverW1, b, invW2, zOverW2, shader);
    }

    /**
     * Draws a line using a simple analytical rasterization algorithm.
     *
     * <p>
     * The dominant axis is selected automatically:
     * </p>
     *
     * <ul>
     * <li>If |dx| > |dy| → iterate over X</li>
     * <li>Otherwise → iterate over Y</li>
     * </ul>
     *
     * <p>
     * Vertex attributes are linearly interpolated along the line.
     * Depth is reconstructed using perspective-correct interpolation.
     * </p>
     *
     * @param a       first line vertex
     * @param invW1   reciprocal clip-space W for first vertex
     * @param zOverW1 depth divided by W for first vertex
     * @param b       second line vertex
     * @param invW2   reciprocal clip-space W for second vertex
     * @param zOverW2 depth divided by W for second vertex
     * @param shader  fragment shader
     */
    private void trivialAlgorithm(Vertex a, double invW1, double zOverW1, Vertex b,
            double invW2, double zOverW2, Shader shader) {

        Lerp<Vertex> lerp = new Lerp<>();

        int ax = (int) Math.round(a.getX());
        int ay = (int) Math.round(a.getY());

        int bx = (int) Math.round(b.getX());
        int by = (int) Math.round(b.getY());

        float t;

        if (Math.abs(by - ay) < Math.abs(bx - ax)) {

            if (bx < ax) {
                Vertex tmpV = a;
                a = b;
                b = tmpV;

                double tmp2;
                tmp2 = invW1;
                invW1 = invW2;
                invW2 = tmp2;
                tmp2 = zOverW1;
                zOverW1 = zOverW2;
                zOverW2 = tmp2;

                int tmp;
                tmp = ax;
                ax = bx;
                bx = tmp;

                tmp = ay;
                ay = by;
                by = tmp;
            }

            float k = (by - ay) / (float) (bx - ax);
            float q = ay - k * ax;

            int startX = Math.max(0, ax);
            int endX = Math.min(zBuffer.getWidth() - 1, bx);

            for (int x = startX; x <= endX; x++) {
                int y = Math.round(k * x + q);

                if (y < 0 || y >= zBuffer.getHeight()) {
                    continue;
                }

                t = (x - ax) / (float) (bx - ax);
                Vertex pixel = lerp.lerp(a, b, t);

                zBuffer.setPixelWithZTest(x, y, computeZ(t, invW1, invW2, zOverW1, zOverW2),
                        shader.getColor(pixel));
            }

        } else {

            if (by < ay) {
                Vertex tmpV = a;
                a = b;
                b = tmpV;

                double tmp2;
                tmp2 = invW1;
                invW1 = invW2;
                invW2 = tmp2;
                tmp2 = zOverW1;
                zOverW1 = zOverW2;
                zOverW2 = tmp2;

                int tmp;
                tmp = ax;
                ax = bx;
                bx = tmp;

                tmp = ay;
                ay = by;
                by = tmp;
            }

            float k = (by - ay) / (float) (bx - ax);
            float q = ay - k * ax;

            int startY = Math.max(0, ay);
            int endY = Math.min(zBuffer.getHeight() - 1, by);

            boolean isInfiniteK = false;
            int x = 0;

            if (Float.isInfinite(k)) {
                x = ax;
                isInfiniteK = true;
            }

            for (int y = startY; y <= endY; y++) {
                if (!isInfiniteK) {
                    x = Math.round((y - q) / k);
                }

                if (x < 0 || x >= zBuffer.getWidth()) {
                    continue;
                }

                t = (y - ay) / (float) (by - ay);
                Vertex pixel = lerp.lerp(a, b, t);

                zBuffer.setPixelWithZTest(x, y, computeZ(t, invW1, invW2, zOverW1, zOverW2),
                        shader.getColor(pixel));
            }
        }
    }

    /**
     * Computes perspective-correct interpolated depth.
     *
     * <p>
     * Depth reconstruction:
     * </p>
     *
     * :contentReference[oaicite:2]{index=2}
     *
     * <p>
     * Linear interpolation is first performed on:
     * </p>
     *
     * <ul>
     * <li>{@code 1 / w}</li>
     * <li>{@code z / w}</li>
     * </ul>
     *
     * <p>
     * The final depth value is reconstructed afterward.
     * </p>
     *
     * @param t       interpolation factor in range [0, 1]
     * @param invW1   reciprocal W of first vertex
     * @param invW2   reciprocal W of second vertex
     * @param zOverW1 depth divided by W for first vertex
     * @param zOverW2 depth divided by W for second vertex
     * @return perspective-correct interpolated depth
     */
    private double computeZ(float t, double invW1, double invW2, double zOverW1, double zOverW2) {
        double invW = invW1 + t * (invW2 - invW1);
        double zOverW = zOverW1 + t * (zOverW2 - zOverW1);

        return zOverW / invW;
    }
}
