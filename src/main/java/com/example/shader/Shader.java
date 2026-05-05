package com.example.shader;

import com.example.model.Vertex;
import com.example.transforms.Col;

public interface Shader {
    Col getColor(Vertex pixel);
}
