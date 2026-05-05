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

        Lerp<Vertex> lerp = new Lerp<>();

        // 1. část
        for (int y = (int) a.getY(); y <= (int) b.getY(); y++) {
            // Hrana AB
            double tAB = (y - a.getY()) / (b.getY() - a.getY());
            Vertex ab = lerp.lerp(a, b, tAB);

            // Hrana AC
            double tAC = (y - a.getY()) / (c.getY() - a.getY());
            Vertex ac = lerp.lerp(a, c, tAC);

            // TODO: kontrola, jestli je ab.getX() < ac.getX()

            for (int x = (int) Math.round(ab.getX()); x <= (int) Math.round(ac.getX()); x++) {
                double t = (x - ab.getX()) / (ac.getX() - ab.getX());
                Vertex pixel = lerp.lerp(ab, ac, t);

                zBuffer.setPixelWithZTest(x, y, pixel.getZ(), shader.getColor(pixel));
            }
        }

        // 2. část

    }
}
