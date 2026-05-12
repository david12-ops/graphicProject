package com.example.model;

import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class RasterVertex {
    public final Vec3D position;
    public final double invW;

    public final double zOverW;

    // future-proof
    public final Point3D worldPos;
    public final Vec3D normal;
    public final Vec2D uv;

    public RasterVertex(
            Vec3D position,
            double invW,
            double zOverW,
            Point3D worldPos,
            Vec3D normal,
            Vec2D uv) {
        this.position = position;
        this.invW = invW;
        this.zOverW = zOverW;
        this.worldPos = worldPos;
        this.normal = normal;
        this.uv = uv;
    }
}
