package com.example.rasterize;

import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

/**
 * Software triangle rasterizer using bounding-box barycentric rasterization
 * with perspective-correct interpolation.
 *
 * <p>
 * The rasterizer:
 * </p>
 *
 * <ol>
 * <li>Rasterizes triangles using a screen-space bounding box</li>
 * <li>Computes edge functions / barycentric coordinates per pixel</li>
 * <li>Performs inside-triangle coverage testing</li>
 * <li>Interpolates depth in screen space</li>
 * <li>Performs perspective-correct interpolation of vertex attributes</li>
 * <li>Executes depth testing</li>
 * <li>Runs fragment shading</li>
 * </ol>
 *
 * <p>
 * Interpolated attributes:
 * </p>
 *
 * <ul>
 * <li>World position</li>
 * <li>Normal</li>
 * <li>Texture coordinates</li>
 * <li>Vertex color</li>
 * </ul>
 *
 * <p>
 * Screen-space positions are assumed to already be dehomogenized
 * (after perspective divide).
 * </p>
 *
 * <p>
 * Perspective-correct interpolation is performed using inverse-W weighting:
 * </p>
 *
 * <p>
 * attribute =
 * (
 * λ0 * attribute0 / w0 +
 * λ1 * attribute1 / w1 +
 * λ2 * attribute2 / w2
 * )
 * /
 * (
 * λ0 / w0 +
 * λ1 / w1 +
 * λ2 / w2
 * )
 * </p>
 *
 * <p>
 * Depth values are interpolated directly in screen/NDC space.
 * </p>
 */
public class TriangleRasterizer {
    private final ZBuffer zBuffer;

    /**
     * Creates a triangle rasterizer.
     *
     * @param zBuffer depth buffer
     */
    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    /**
     * Rasterizes a triangle using scanline rasterization.
     *
     * <p>
     * The triangle is first sorted by Y coordinate and then processed
     * in two scanline sections:
     * </p>
     *
     * <ul>
     * <li>Upper half (A → B)</li>
     * <li>Lower half (B → C)</li>
     * </ul>
     *
     * <p>
     * For each pixel:
     * </p>
     *
     * <ol>
     * <li>Barycentric coordinates are computed</li>
     * <li>Perspective-correct interpolation is performed</li>
     * <li>Depth is evaluated</li>
     * <li>The fragment shader computes final color</li>
     * </ol>
     *
     * @param a      first triangle vertex
     * @param b      second triangle vertex
     * @param c      third triangle vertex
     * @param shader fragment shader
     */
    public void rasterize(RasterVertex a, RasterVertex b, RasterVertex c, Shader shader) {
        double area = (b.getPosition().getX() - a.getPosition().getX())
                * (c.getPosition().getY() - a.getPosition().getY()) -
                (b.getPosition().getY() - a.getPosition().getY()) * (c.getPosition().getX() - a.getPosition().getX());

        if (Math.abs(area) < 1e-8) {
            return;
        }

        int minX = (int) Math
                .floor(Math.min(a.getPosition().getX(), Math.min(b.getPosition().getX(), c.getPosition().getX())));
        int maxX = (int) Math
                .ceil(Math.max(a.getPosition().getX(), Math.max(b.getPosition().getX(), c.getPosition().getX())));

        int minY = (int) Math
                .floor(Math.min(a.getPosition().getY(), Math.min(b.getPosition().getY(), c.getPosition().getY())));
        int maxY = (int) Math
                .ceil(Math.max(a.getPosition().getY(), Math.max(b.getPosition().getY(), c.getPosition().getY())));

        Vec2D posA = new Vec2D(a.getPosition().getX(), a.getPosition().getY());
        Vec2D posB = new Vec2D(b.getPosition().getX(), b.getPosition().getY());
        Vec2D posC = new Vec2D(c.getPosition().getX(), c.getPosition().getY());

        double invArea = 1.0 / area;

        int startX = Math.max(0, minX);
        int endX = Math.min(zBuffer.getWidth(), maxX);

        int startY = Math.max(0, minY);
        int endY = Math.min(zBuffer.getHeight(), maxY);

        if (startX >= endX || startY >= endY) {
            return;
        }

        double EPS = 1e-6;
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                double px = x + 0.5;
                double py = y + 0.5;

                double w0 = edge(posB, posC, px, py) * invArea;
                double w1 = edge(posC, posA, px, py) * invArea;
                double w2 = edge(posA, posB, px, py) * invArea;

                boolean inside = (w0 >= -EPS && w1 >= -EPS && w2 >= -EPS);

                // Coverage test can do cracks
                if (inside) {
                    double baryInvW = w0 * a.getInvW() +
                            w1 * b.getInvW() +
                            w2 * c.getInvW();

                    // interpolate
                    double depth = a.getZ() * w0 +
                            b.getZ() * w1 +
                            c.getZ() * w2;

                    Vec3D baryNormal = a.getNormalOverW().mul(w0)
                            .add(b.getNormalOverW().mul(w1))
                            .add(c.getNormalOverW().mul(w2))
                            .mul(1.0 / baryInvW)
                            .normalized()
                            .orElse(new Vec3D(0, 0, 1));

                    Vec3D baryWorldPos = a.getWorldPosOverW().mul(w0)
                            .add(b.getWorldPosOverW().mul(w1))
                            .add(c.getWorldPosOverW().mul(w2))
                            .mul(1.0 / baryInvW);

                    Vec2D baryUV = a.getUvOverW().mul(w0)
                            .add(b.getUvOverW().mul(w1))
                            .add(c.getUvOverW().mul(w2))
                            .mul(1.0 / baryInvW);

                    Col baryColor = a.getColorOverW().mul(w0)
                            .add(b.getColorOverW().mul(w1))
                            .add(c.getColorOverW().mul(w2))
                            .mul(1.0 / baryInvW);

                    Vertex pixel = new Vertex(new Point3D(new Vec3D(px, py, depth)),
                            baryWorldPos, baryColor, baryUV,
                            baryNormal);

                    // zbuffer
                    zBuffer.setPixelWithZTest(
                            x,
                            y,
                            depth,
                            shader.getColor(pixel).saturate());
                }
            }
        }
    }

    double edge(Vec2D a, Vec2D b, double px, double py) {
        return (b.getX() - a.getX()) * (py - a.getY())
                - (b.getY() - a.getY()) * (px - a.getX());
    }
}
