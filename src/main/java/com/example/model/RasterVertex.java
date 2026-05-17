package com.example.model;

import com.example.transforms.Col;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

/**
 * Represents a rasterization-ready vertex used during triangle rasterization.
 *
 * <p>
 * A {@code RasterVertex} contains screen-space coordinates together with
 * attributes prepared for perspective-correct interpolation.
 * </p>
 *
 * <p>
 * Most interpolated attributes are stored in the form:
 * </p>
 *
 * :contentReference[oaicite:0]{index=0}
 *
 * <p>
 * allowing reconstruction during rasterization using:
 * </p>
 *
 * :contentReference[oaicite:1]{index=1}
 *
 * <p>
 * Stored attributes:
 * </p>
 *
 * <ul>
 * <li>Screen-space position</li>
 * <li>Reciprocal clip-space W ({@code 1 / w})</li>
 * <li>Depth value</li>
 * <li>World position divided by W</li>
 * <li>Normal vector divided by W</li>
 * <li>Texture coordinates divided by W</li>
 * <li>Color divided by W</li>
 * </ul>
 *
 * <p>
 * The class is immutable and optimized for rasterization.
 * </p>
 */
public class RasterVertex {
    private final Vec3D position;
    private final double invW;

    private final double z;

    // future-proof
    private final Vec3D worldPosOverW;
    private final Vec3D normalOverW;
    private final Vec2D uvOverW;

    private Col colorOverW;

    public RasterVertex(
            Vec3D position,
            double invW,
            double z,
            Vec3D worldPosOverW,
            Vec3D normalOverW,
            Vec2D uvOverW,
            Col colorOverW) {

        this.position = position;
        this.invW = invW;
        this.z = z;
        this.worldPosOverW = worldPosOverW;
        this.normalOverW = normalOverW;
        this.uvOverW = uvOverW;
        this.colorOverW = colorOverW;
    }

    // POSITION
    public Vec3D getPosition() {
        return position;
    }

    // COLOR W
    public Col getColorOverW() {
        return colorOverW;
    }

    // INV W
    public double getInvW() {
        return invW;
    }

    // Z OVER W
    public double getZ() {
        return z;
    }

    // WORLD POSITION
    public Vec3D getWorldPosOverW() {
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
