package com.example.model;

import com.example.transforms.Col;
import com.example.transforms.Vec3D;

/**
 * Represents a point light source in 3D space.
 *
 * <p>
 * The light contains:
 * </p>
 *
 * <ul>
 * <li>World-space position</li>
 * <li>Light color/intensity</li>
 * </ul>
 *
 * <p>
 * The light is typically used in lighting models such as:
 * </p>
 *
 * <ul>
 * <li>Phong shading</li>
 * <li>Blinn-Phong shading</li>
 * <li>Diffuse lighting</li>
 * <li>Specular highlights</li>
 * </ul>
 *
 * <p>
 * Light direction toward a fragment is commonly computed as:
 * </p>
 *
 * :contentReference[oaicite:0]{index=0}
 */
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
