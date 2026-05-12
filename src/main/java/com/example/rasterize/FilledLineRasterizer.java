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
 * Line rasterizer using a simple (trivial) line drawing algorithm.
 * 
 * Supports solid and gradient coloring, endpoint visualization,
 * and snapping to horizontal, vertical, or diagonal directions.
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
     * Rasterizes a line between two 3D vertices using the selected rasterization
     * mode.
     *
     * The X and Y coordinates are used for rasterization (screen space).
     * The Z coordinate can be used for depth testing (Z-buffer), but is not handled
     * here.
     *
     * @param a start vertex of the line in 3D space
     * @param b end vertex of the line in 3D space
     */
    @Override
    public void rasterize(Vertex a, double invW1, double zOverW1, Vertex b, double invW2, double zOverW2,
            Shader shader) {
        trivialAlgorithm(a, invW1, zOverW1, b, invW2, zOverW2, shader);
    }

    /**
     * Draws a line using a simple analytical (trivial) algorithm.
     *
     * Chooses the dominant axis (X or Y) to avoid gaps
     * and supports both solid and gradient coloring.
     * Implemented clip the for cycle to only valid bounds
     * {@code Math.max(0, x1); Math.min(raster.getWidth() - 1, x2);} - for X,
     * {@code Math.max(0, y1); Math.min(raster.getHeight() - 1, y2);} - for Y
     * 
     * @param x1 Start x-coordinate
     * @param y1 Start y-coordinate
     * @param x2 End x-coordinate
     * @param y2 End y-coordinate
     * @param z  Depth value (z-coordinate)
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

    private double computeZ(float t, double invW1, double invW2, double zOverW1, double zOverW2) {
        double invW = invW1 + t * (invW2 - invW1);
        double zOverW = zOverW1 + t * (zOverW2 - zOverW1);

        return zOverW / invW;
    }
}
