package com.example.model;

import com.example.transforms.Col;
import com.example.transforms.Point3D;

public class Light {
    final Point3D position;
    final Col color;

    public Light(Point3D position, Col color) {
        this.position = position;
        this.color = color;
    }

    public Col getColor() {
        return color;
    }

    public Point3D getPosition() {
        return position;
    }
}
