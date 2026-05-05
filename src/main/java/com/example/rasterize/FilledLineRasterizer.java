package com.example.rasterize;

import com.example.enums.ColorMode;
import com.example.enums.RasterizerMode;
import com.example.model.Line;
import com.example.raster.ZBuffer;
import com.example.transforms.Col;
import com.example.transforms.Vec3D;

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
     * If NORMAL mode is selected, the trivial line rasterization algorithm is used.
     * SHIFT mode is not supported for 3D vertices.
     *
     * @param a start vertex of the line in 3D space
     * @param b end vertex of the line in 3D space
     */
    @Override
    public void rasterize(Vec3D a, Vec3D b) {
        if (rasterizerMode == RasterizerMode.NORMAL) {
            trivialAlgorithm((int) Math.round(a.getX()), (int) Math.round(a.getY()), a.getZ(),
                    (int) Math.round(b.getX()),
                    (int) Math.round(b.getY()), b.getZ());
        } else if (rasterizerMode == RasterizerMode.SHIFT) {
            System.out.println("Shift mode is not supported for 3D points. Rasterizing without snapping.");
        } else
            return;
    }

    /**
     * Rasterizes a line model object.
     *
     * @param line Line to rasterize
     */
    @Override
    public void rasterize(Line line) {
        rasterize(line.getPointA(), line.getPointB());
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
    private void trivialAlgorithm(int x1, int y1, double z1, int x2, int y2, double z2) {
        if (!(isGradientUsed() || isSolidUsed())) {
            System.out.println(
                    "Color mode is invalid or missing colors to draw.");
            System.out.println("Check if colors are set with color mode that use them.");
            return;
        }

        // y = kx + q
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        float t = 0;

        if (Math.abs((y2 - y1)) < Math.abs(x2 - x1)) {

            if (x2 < x1) {
                int p;
                p = x1;
                x1 = x2;
                x2 = p;
                p = y1;
                y1 = y2;
                y2 = p;
            }

            int startX = Math.max(0, x1);
            int endX = Math.min(zBuffer.getImageBufferWidth() - 1, x2);

            if (isSolidUsed()) {

                for (int x = startX; x < endX; x++) {
                    int y = Math.round(k * x + q);

                    t = (x - x1) / (float) (x2 - x1);

                    zBuffer.setPixelWithZTest(x, y, computeZ(t, z1, z2),
                            selectedColor == null ? solidColor : selectedColor);
                }
            } else {

                for (int x = startX; x < endX; x++) {
                    int y = Math.round(k * x + q);

                    t = (x - x1) / (float) (x2 - x1);

                    zBuffer.setPixelWithZTest(x, y, computeZ(t, z1, z2),
                            selectedColor == null ? computeColor(t, startColor, endColor) : selectedColor);
                }
            }

        } else {

            if (y2 < y1) {
                int p;
                p = x1;
                x1 = x2;
                x2 = p;
                p = y1;
                y1 = y2;
                y2 = p;
            }

            int startY = Math.max(0, y1);
            int endY = Math.min(zBuffer.getImageBufferHeight() - 1, y2);
            boolean isInfiniteK = false;
            int x = 0;

            if (Float.isInfinite(k)) {
                x = x1;
                isInfiniteK = true;
            }

            if (isSolidUsed()) {

                for (int y = startY; y < endY; y++) {
                    if (!isInfiniteK) {
                        x = Math.round((y - q) / k);
                    }

                    t = (y - y1) / (float) (y2 - y1);

                    zBuffer.setPixelWithZTest(x, y, computeZ(t, z1, z2),
                            selectedColor == null ? solidColor : selectedColor);
                }
            } else {

                for (int y = startY; y < endY; y++) {
                    if (!isInfiniteK) {
                        x = Math.round((y - q) / k);
                    }

                    t = (y - y1) / (float) (y2 - y1);

                    zBuffer.setPixelWithZTest(x, y, computeZ(t, z1, z2),
                            selectedColor == null ? computeColor(t, startColor, endColor) : selectedColor);
                }
            }
        }
    }

    /**
     * Computes interpolated color between two colors.
     *
     * @param t          Interpolation factor in range ⟨0,1⟩
     * @param startColor Starting color
     * @param endColor   Ending color
     * @return Interpolated color as a Col object
     */
    private Col computeColor(float t, Col startColor, Col endColor) {
        if (t < 0f)
            t = 0f;
        else if (t > 1f)
            t = 1f;

        double it = 1.0 - t;

        // For each channel (A, R, G, B):
        // C=C1​(1−t)+C2​t - formula for linear interpolation of colors
        int a = (int) Math.round((startColor.getA() * it + endColor.getA() * t) * 255.0);
        int r = (int) Math.round((startColor.getR() * it + endColor.getR() * t) * 255.0);
        int g = (int) Math.round((startColor.getG() * it + endColor.getG() * t) * 255.0);
        int b = (int) Math.round((startColor.getB() * it + endColor.getB() * t) * 255.0);

        // revert back
        return new Col(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
    }

    /**
     * Checks whether gradient color mode is correctly configured.
     */
    private boolean isGradientUsed() {
        return colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null);
    }

    /**
     * Checks whether solid color mode is correctly configured.
     */
    private boolean isSolidUsed() {
        return colorMode == ColorMode.SOLID && solidColor != null;
    }

    private double computeZ(float t, double z1, double z2) {
        return z1 + t * (z2 - z1);
    }
}
