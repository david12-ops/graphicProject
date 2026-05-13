package com.example.shader;

import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Vec3D;

public interface Shader {
    Col getColor(Vertex pixel);

    default void setCameraPosition(Vec3D cameraPosition) {

    };
}
