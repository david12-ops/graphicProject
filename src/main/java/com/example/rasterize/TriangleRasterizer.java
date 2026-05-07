package com.example.rasterize;

import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;
import com.example.utils.Lerp;

public class TriangleRasterizer {
    private final ZBuffer zBuffer;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(Vertex a, Vertex b, Vertex c, Shader shader) {
        // TODO: seřadit vrcholy podle y od min po max
        // ab
        if (a.getY() > b.getY()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }
        // bc
        if (b.getY() > c.getY()) {
            Vertex temp = b;
            b = c;
            c = temp;
        }
        // ab
        if (a.getY() > b.getY()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }

        System.out.println("Rasterizing triangle with vertices: " + a + ", " + b + ", " + c);

        Lerp<Vertex> lerp = new Lerp<>();

        // 1. část
        for (int y = (int) a.getY(); y < (int) b.getY(); y++) {
            // Hrana AB
            double dyAB = b.getY() - a.getY();
            double tAB = 0;
            if (dyAB != 0) {
                tAB = (y - a.getY()) / (b.getY() - a.getY());
            }
            Vertex ab = lerp.lerp(a, b, tAB);

            // Hrana AC
            double dyAC = c.getY() - a.getY();
            double tAC = 0;
            if (dyAC != 0) {
                tAC = (y - a.getY()) / (c.getY() - a.getY());
            }
            Vertex ac = lerp.lerp(a, c, tAC);

            // TODO: kontrola, jestli je ab.getX() < ac.getX()
            if (ab.getX() > ac.getX()) {
                Vertex temp = ab;
                ab = ac;
                ac = temp;
            }

            for (int x = (int) Math.round(ab.getX()); x <= (int) Math.round(ac.getX()); x++) {
                double dx = ac.getX() - ab.getX();
                if (dx == 0) {
                    Vertex pixel = ab;
                    zBuffer.setPixelWithZTest(
                            x,
                            y,
                            pixel.getZ(),
                            shader.getColor(pixel));
                    continue;
                }

                double t = (x - ab.getX()) / dx;
                Vertex pixel = lerp.lerp(ab, ac, t);

                zBuffer.setPixelWithZTest(x, y, pixel.getZ(), shader.getColor(pixel));
            }
        }

        // 2. část
        // B -> C
        // A -> C
        for (int y = (int) b.getY(); y <= (int) c.getY(); y++) {
            // Hrana BC
            double dyBC = c.getY() - b.getY();
            double tBC = 0;
            if (dyBC != 0) {
                tBC = (y - b.getY()) / (c.getY() - b.getY());
            }
            Vertex bc = lerp.lerp(b, c, tBC);

            // Hrana AC
            double dyAC = c.getY() - a.getY();
            double tAC = 0;
            if (dyAC != 0) {
                tAC = (y - a.getY()) / (c.getY() - a.getY());
            }
            Vertex ac = lerp.lerp(a, c, tAC);

            // TODO: kontrola, jestli je bc.getX() < ac.getX()
            if (bc.getX() > ac.getX()) {
                Vertex temp = bc;
                bc = ac;
                ac = temp;
            }

            for (int x = (int) Math.round(bc.getX()); x <= (int) Math.round(ac.getX()); x++) {
                double dx = ac.getX() - bc.getX();
                if (dx == 0) {
                    Vertex pixel = bc;
                    zBuffer.setPixelWithZTest(
                            x,
                            y,
                            pixel.getZ(),
                            shader.getColor(pixel));
                    continue;
                }

                double t = (x - bc.getX()) / dx;
                Vertex pixel = lerp.lerp(bc, ac, t);

                zBuffer.setPixelWithZTest(x, y, pixel.getZ(), shader.getColor(pixel));
            }
        }
    }
}
