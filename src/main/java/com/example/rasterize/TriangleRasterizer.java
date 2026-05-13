package com.example.rasterize;

import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class TriangleRasterizer {
    // neni ready
    private final ZBuffer zBuffer;
    // private boolean disableShader = false;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(RasterVertex a, RasterVertex b, RasterVertex c, Shader shader) {
        // TODO: seřadit vrcholy podle y od min po max
        // ab
        if (a.getPosition().getY() > b.getPosition().getY()) {
            RasterVertex temp = a;
            a = b;
            b = temp;
        }
        // bc
        if (b.getPosition().getY() > c.getPosition().getY()) {
            RasterVertex temp = b;
            b = c;
            c = temp;
        }
        // ab
        if (a.getPosition().getY() > b.getPosition().getY()) {
            RasterVertex temp = a;
            a = b;
            b = temp;
        }

        double denom = (b.getPosition().getY() - c.getPosition().getY()) *
                (a.getPosition().getX() - c.getPosition().getX()) +

                (c.getPosition().getX() - b.getPosition().getX()) *
                        (a.getPosition().getY() - c.getPosition().getY());

        if (Math.abs(denom) < 1e-8) {
            return;
        }

        double invDenom = 1.0 / denom;

        int yStart = (int) Math.ceil(a.getPosition().getY() - 0.5);
        int yMiddle = (int) Math.ceil(b.getPosition().getY() - 0.5);
        int yEnd = (int) Math.ceil(c.getPosition().getY() - 0.5);

        // 1. část
        for (int y = yStart; y < yMiddle; y++) {

            if (y < 0 || y >= zBuffer.getHeight()) {
                continue;
            }

            // Hrana AB
            double dyAB = b.getPosition().getY() - a.getPosition().getY();
            double tAB = 0;
            if (dyAB != 0) {
                tAB = ((y + 0.5) - a.getPosition().getY()) / (b.getPosition().getY() - a.getPosition().getY());
            }
            double abX = lerp(a.getPosition().getX(), b.getPosition().getX(), tAB);

            // Hrana AC
            double dyAC = c.getPosition().getY() - a.getPosition().getY();
            double tAC = 0;
            if (dyAC != 0) {
                tAC = ((y + 0.5) - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            }
            double acX = lerp(a.getPosition().getX(), c.getPosition().getX(), tAC);

            // TODO: kontrola, jestli je ab.getX() < ac.getX()
            if (abX > acX) {
                double temp = abX;
                abX = acX;
                acX = temp;
            }

            int xLeft = (int) Math.ceil(abX - 0.5);
            int xRight = (int) Math.ceil(acX - 0.5);

            int startX = Math.max(0, xLeft);
            int endX = Math.min(zBuffer.getWidth(), xRight);

            if (startX >= endX) {
                continue;
            }

            double py = y + 0.5;
            for (int x = startX; x < endX; x++) {
                double px = x + 0.5;

                double[] bary = computeBarycentric(a, b, c, px, py, denom);

                double w0 = bary[0] * invDenom;
                double w1 = bary[1] * invDenom;
                double w2 = bary[2] * invDenom;

                double baryInvW = w0 * a.getInvW() +
                        w1 * b.getInvW() +
                        w2 * c.getInvW();

                double baryZOverW = w0 * a.getZOverW() +
                        w1 * b.getZOverW() +
                        w2 * c.getZOverW();

                Vec3D baryNormal = a.getNormalOverW().mul(w0)
                        .add(b.getNormalOverW().mul(w1))
                        .add(c.getNormalOverW().mul(w2))
                        .mul(1.0 / baryInvW).normalized().orElse(new Vec3D(0, 0, 1));

                Point3D baryWorldPos = a.getWorldPosOverW().mul(w0)
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

                double depth = baryZOverW / baryInvW;

                Vertex pixel = new Vertex(new Point3D(new Vec3D(px, py, depth)), baryWorldPos, baryColor, baryUV,
                        baryNormal);

                zBuffer.setPixelWithZTest(
                        x,
                        y,
                        depth,
                        shader.getColor(pixel));
            }
        }

        // 2. část
        // B -> C
        // A -> C
        for (int y = yMiddle; y < yEnd; y++) {

            if (y < 0 || y >= zBuffer.getHeight()) {
                continue;
            }

            // Hrana BC
            double dyBC = c.getPosition().getY() - b.getPosition().getY();
            double tBC = 0;
            if (dyBC != 0) {
                tBC = ((y + 0.5) - b.getPosition().getY()) / (c.getPosition().getY() - b.getPosition().getY());
            }
            double bcX = lerp(b.getPosition().getX(), c.getPosition().getX(), tBC);

            // Hrana AC
            double dyAC = c.getPosition().getY() - a.getPosition().getY();
            double tAC = 0;
            if (dyAC != 0) {
                tAC = ((y + 0.5) - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            }
            double acX = lerp(a.getPosition().getX(), c.getPosition().getX(), tAC);

            // TODO: kontrola, jestli je bc.getX() < ac.getX()
            if (bcX > acX) {
                double temp = bcX;
                bcX = acX;
                acX = temp;
            }

            int xLeft = (int) Math.ceil(bcX - 0.5);
            int xRight = (int) Math.ceil(acX - 0.5);

            int startX = Math.max(0, xLeft);
            int endX = Math.min(zBuffer.getWidth(), xRight);

            if (startX >= endX) {
                continue;
            }

            double py = y + 0.5;
            for (int x = startX; x < endX; x++) {
                double px = x + 0.5;

                double[] bary = computeBarycentric(a, b, c, px, py, denom);

                double w0 = bary[0] * invDenom;
                double w1 = bary[1] * invDenom;
                double w2 = bary[2] * invDenom;

                double baryInvW = w0 * a.getInvW() +
                        w1 * b.getInvW() +
                        w2 * c.getInvW();

                double baryZOverW = w0 * a.getZOverW() +
                        w1 * b.getZOverW() +
                        w2 * c.getZOverW();

                Vec3D baryNormal = a.getNormalOverW().mul(w0)
                        .add(b.getNormalOverW().mul(w1))
                        .add(c.getNormalOverW().mul(w2))
                        .mul(1.0 / baryInvW).normalized().orElse(new Vec3D(0, 0, 1));

                Point3D baryWorldPos = a.getWorldPosOverW().mul(w0)
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

                double depth = baryZOverW / baryInvW;

                Vertex pixel = new Vertex(new Point3D(new Vec3D(px, py, depth)), baryWorldPos, baryColor, baryUV,
                        baryNormal);

                zBuffer.setPixelWithZTest(
                        x,
                        y,
                        depth,
                        shader.getColor(pixel));
            }
        }
    }

    private double[] computeBarycentric(
            RasterVertex v0,
            RasterVertex v1,
            RasterVertex v2,
            double px,
            double py, double denom) {

        double w0 = ((v1.getPosition().getY() - v2.getPosition().getY()) *
                (px - v2.getPosition().getX()) +

                (v2.getPosition().getX() - v1.getPosition().getX()) *
                        (py - v2.getPosition().getY()));

        double w1 = ((v2.getPosition().getY() - v0.getPosition().getY()) *
                (px - v0.getPosition().getX()) +

                (v0.getPosition().getX() - v2.getPosition().getX()) *
                        (py - v0.getPosition().getY()));

        double w2 = ((v0.getPosition().getY() - v1.getPosition().getY()) *
                (px - v1.getPosition().getX()) +

                (v1.getPosition().getX() - v0.getPosition().getX()) *
                        (py - v1.getPosition().getY()));

        return new double[] { w0, w1, w2 };
    }

    private double lerp(double value1, double value2, double t) {
        return value1 + t * (value2 - value1);
    }
}
