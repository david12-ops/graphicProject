package com.example.model;

import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class RasterVertex {
    private final Vec3D position;
    private final double invW;

    private final double zOverW;

    // future-proof
    private final Point3D worldPosOverW;
    private final Vec3D normalOverW;
    private final Vec2D uvOverW;

    private Col colorOverW;

    public RasterVertex(
            Vec3D position,
            double invW,
            double zOverW,
            Point3D worldPosOverW,
            Vec3D normalOverW,
            Vec2D uvOverW,
            Col colorOverW) {

        this.position = position;
        this.invW = invW;
        this.zOverW = zOverW;
        this.worldPosOverW = worldPosOverW;
        this.normalOverW = normalOverW;
        this.uvOverW = uvOverW;
        this.colorOverW = colorOverW;
    }

    // POSITION
    public Vec3D getPosition() {
        return position;
    }

    // CLOR
    public Col getColorOverW() {
        return colorOverW;
    }

    public void setColor(Col colorOverW) {
        this.colorOverW = colorOverW;
    }

    // INV W
    public double getInvW() {
        return invW;
    }

    // Z OVER W
    public double getZOverW() {
        return zOverW;
    }

    // WORLD POSITION
    public Point3D getWorldPosOverW() {
        return worldPosOverW;
    }

    // NORMAL
    public Vec3D getNormalOverW() {
        return normalOverW;
    }

    // UV
    public Vec2D getUvOverW() {
        return uvOverW;
    }
}
