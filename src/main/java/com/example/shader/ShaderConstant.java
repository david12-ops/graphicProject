package com.example.shader;

import com.example.model.Vertex;
import com.example.transforms.Col;

public class ShaderConstant implements Shader {
    @Override
    public Col getColor(Vertex pixel) {
        return new Col(0xff0000);
    }
}
