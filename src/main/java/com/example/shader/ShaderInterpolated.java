package com.example.shader;

import com.example.model.Vertex;
import com.example.transforms.Col;

public class ShaderInterpolated implements Shader {
    @Override
    public Col getColor(Vertex pixel) {
        return pixel.getColor();
    }
}
