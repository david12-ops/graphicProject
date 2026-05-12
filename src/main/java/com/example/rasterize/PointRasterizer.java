package com.example.rasterize;

import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;

public class PointRasterizer {

    private ZBuffer zBuffer;

    public PointRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(Vertex vertex, Shader shader) {
        zBuffer.setPixelWithZTest(
                (int) vertex.getX(),
                (int) vertex.getY(),
                vertex.getZ(),
                shader.getColor(vertex));
    }
}
