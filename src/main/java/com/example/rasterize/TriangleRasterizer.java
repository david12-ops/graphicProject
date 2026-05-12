package com.example.rasterize;

import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;
import com.example.transforms.Vec3D;
import com.example.utils.Lerp;

public class TriangleRasterizer {
    // neni ready
    private final ZBuffer zBuffer;
    // private boolean disableShader = false;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(RasterVertex a, RasterVertex b, RasterVertex c) {
        // TODO: seřadit vrcholy podle y od min po max
        // ab
        if (a.getY() > b.getY()) {
            Vertex temp = a;
            a = b;
            b = temp;

            double tempInvW = invW1;
            invW1 = invW2;
            invW2 = tempInvW;

            double tempZ = zOverW1;
            zOverW1 = zOverW2;
            zOverW2 = tempZ;
        }
        // bc
        if (b.getY() > c.getY()) {
            Vertex temp = b;
            b = c;
            c = temp;

            double tempInvW = invW2;
            invW2 = invW3;
            invW3 = tempInvW;

            double tempZ = zOverW2;
            zOverW2 = zOverW3;
            zOverW3 = tempZ;
        }
        // ab
        if (a.getY() > b.getY()) {
            Vertex temp = a;
            a = b;
            b = temp;

            double tempInvW = invW1;
            invW1 = invW2;
            invW2 = tempInvW;

            double tempZ = zOverW1;
            zOverW1 = zOverW2;
            zOverW2 = tempZ;
        }

        Lerp<Vertex> lerp = new Lerp<>();
        int ySplit = (int) Math.floor(b.getY());
        int minY = (int) Math.floor(Math.min(a.getY(), Math.min(b.getY(), c.getY())));
        int maxY = (int) Math.ceil(Math.max(a.getY(), Math.max(b.getY(), c.getY())));

        // 1. část
        for (int y = minY; y <= ySplit; y++) {

            if (y < 0 || y >= zBuffer.getHeight()) {
                continue;
            }

            // Hrana AB
            double dyAB = b.getY() - a.getY();
            double tAB = 0;
            if (dyAB != 0) {
                tAB = ((y + 0.5) - a.getY()) / (b.getY() - a.getY());
            }
            Vertex ab = lerp.lerp(a, b, tAB);

            // Hrana AC
            double dyAC = c.getY() - a.getY();
            double tAC = 0;
            if (dyAC != 0) {
                tAC = ((y + 0.5) - a.getY()) / (c.getY() - a.getY());
            }
            Vertex ac = lerp.lerp(a, c, tAC);

            double invW_AB = lerp(invW1, invW2, tAB);
            double invW_AC = lerp(invW1, invW3, tAC);

            double zOverW_AB = lerp(zOverW1, zOverW2, tAB);
            double zOverW_AC = lerp(zOverW1, zOverW3, tAC);

            // TODO: kontrola, jestli je ab.getX() < ac.getX()
            if (ab.getX() > ac.getX()) {
                Vertex temp = ab;
                ab = ac;
                ac = temp;

                double tempInvW = invW_AB;
                invW_AB = invW_AC;
                invW_AC = tempInvW;

                double tempZ = zOverW_AB;
                zOverW_AB = zOverW_AC;
                zOverW_AC = tempZ;
            }

            int xLeft = (int) Math.ceil(ab.getX());
            int xRight = (int) Math.floor(ac.getX());

            int startX = Math.max(0, xLeft);
            int endX = Math.min(zBuffer.getWidth() - 1, xRight);

            if (startX > endX) {

                int px = (int) Math.round((ab.getX() + ac.getX()) * 0.5);

                if (px >= 0 && px < zBuffer.getWidth()) {

                    zBuffer.setPixelWithZTest(
                            px,
                            y,
                            computeZ(
                                    0.5,
                                    invW_AB,
                                    invW_AC,
                                    zOverW_AB,
                                    zOverW_AC),
                            shader.getColor(lerp.lerp(ab, ac, 0.5)));
                }

                continue;
            }

            double dx = ac.getX() - ab.getX();

            if (Math.abs(dx) < 1e-8) {

                int px = (int) Math.round(ab.getX());

                if (px >= 0 && px < zBuffer.getWidth()) {

                    zBuffer.setPixelWithZTest(
                            px,
                            y,
                            computeZ(
                                    0,
                                    invW_AB,
                                    invW_AC,
                                    zOverW_AB,
                                    zOverW_AC),
                            shader.getColor(ab));
                }

                continue;
            }

            for (int x = startX; x <= endX; x++) {
                double t = ((x + 0.5) - ab.getX()) / (ac.getX() - ab.getX());
                t = Math.max(0.0, Math.min(1.0, t));

                Vertex pixel = lerp.lerp(ab, ac, t);

                Vec3D spA = ab.getShadingPosition();
                Vec3D spB = ac.getShadingPosition();

                Vec3D shadingPos = spA.mul(1 - t).add(spB.mul(t));
                pixel.setShadingPosition(shadingPos);

                zBuffer.setPixelWithZTest(x, y, computeZ(
                        t,
                        invW_AB, invW_AC,
                        zOverW_AB, zOverW_AC),
                        shader.getColor(pixel));
            }
        }

        // 2. část
        // B -> C
        // A -> C
        for (int y = ySplit + 1; y <= maxY; y++) {

            if (y < 0 || y >= zBuffer.getHeight()) {
                continue;
            }

            // Hrana BC
            double dyBC = c.getY() - b.getY();
            double tBC = 0;
            if (dyBC != 0) {
                tBC = ((y + 0.5) - b.getY()) / (c.getY() - b.getY());
            }
            Vertex bc = lerp.lerp(b, c, tBC);

            // Hrana AC
            double dyAC = c.getY() - a.getY();
            double tAC = 0;
            if (dyAC != 0) {
                tAC = ((y + 0.5) - a.getY()) / (c.getY() - a.getY());
            }
            Vertex ac = lerp.lerp(a, c, tAC);

            double invW_BC = lerp(invW2, invW3, tBC);
            double invW_AC = lerp(invW1, invW3, tAC);

            double zOverW_BC = lerp(zOverW2, zOverW3, tBC);
            double zOverW_AC = lerp(zOverW1, zOverW3, tAC);

            // TODO: kontrola, jestli je bc.getX() < ac.getX()
            if (bc.getX() > ac.getX()) {
                Vertex temp = bc;
                bc = ac;
                ac = temp;

                double tempInvW = invW_BC;
                invW_BC = invW_AC;
                invW_AC = tempInvW;

                double tempZ = zOverW_BC;
                zOverW_BC = zOverW_AC;
                zOverW_AC = tempZ;
            }

            int xLeft = (int) Math.ceil(bc.getX());
            int xRight = (int) Math.floor(ac.getX());

            int startX = Math.max(0, xLeft);
            int endX = Math.min(zBuffer.getWidth() - 1, xRight);

            if (startX > endX) {

                int px = (int) Math.round((bc.getX() + ac.getX()) * 0.5);

                if (px >= 0 && px < zBuffer.getWidth()) {

                    zBuffer.setPixelWithZTest(
                            px,
                            y,
                            computeZ(
                                    0.5,
                                    invW_BC,
                                    invW_AC,
                                    zOverW_BC,
                                    zOverW_AC),
                            shader.getColor(lerp.lerp(bc, ac, 0.5)));
                }

                continue;
            }

            double dx = ac.getX() - bc.getX();

            if (Math.abs(dx) < 1e-8) {

                int px = (int) Math.round(bc.getX());

                if (px >= 0 && px < zBuffer.getWidth()) {

                    zBuffer.setPixelWithZTest(
                            px,
                            y,
                            computeZ(
                                    0,
                                    invW_BC,
                                    invW_AC,
                                    zOverW_BC,
                                    zOverW_AC),
                            shader.getColor(bc));
                }

                continue;
            }

            for (int x = startX; x <= endX; x++) {
                double t = ((x + 0.5) - bc.getX()) / (ac.getX() - bc.getX());
                t = Math.max(0.0, Math.min(1.0, t));

                Vertex pixel = lerp.lerp(bc, ac, t);

                Vec3D spB = bc.getShadingPosition();
                Vec3D spC = ac.getShadingPosition();

                Vec3D shadingPos = spB.mul(1 - t).add(spC.mul(t));
                pixel.setShadingPosition(shadingPos);

                zBuffer.setPixelWithZTest(x, y, computeZ(t, invW_BC, invW_AC, zOverW_BC, zOverW_AC),
                        shader.getColor(pixel));
            }
        }
    }

    private double computeZ(double t,
            double invW1, double invW2,
            double zOverW1, double zOverW2) {

        double invW = invW1 + t * (invW2 - invW1);
        double zOverW = zOverW1 + t * (zOverW2 - zOverW1);

        return zOverW / invW;
    }

    private double lerp(double value1, double value2, double t) {
        return value1 + t * (value2 - value1);
    }
}
