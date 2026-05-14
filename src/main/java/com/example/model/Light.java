package com.example.model;

import com.example.transforms.Col;
import com.example.transforms.Vec3D;

public class Light {
    final Vec3D position;
    final Col color;

    public Light(Vec3D position, Col color) {
        this.position = position;
        this.color = color;
    }

    public Col getColor() {
        return color;
    }

    public Vec3D getPosition() {
        return position;
    }
}
