package com.example.rasterize;

import com.example.model.Vertex;
import com.example.raster.ZBuffer;
import com.example.shader.Shader;

public class LineRasterizer {

    protected ZBuffer zBuffer;

    public LineRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(Vertex a, double invW1, double zOverW1, Vertex b, double invW2, double zOverW2,
            Shader shader) {

    }
}