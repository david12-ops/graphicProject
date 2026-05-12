package com.example.utils;

import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class RasterVertexBuilder {

    public static RasterVertex from(Vertex v) {

        double invW = 1.0 / v.getPosition().getW();

        Vec3D pos = v.getPosition().ignoreW();

        double zOverW = v.getPosition().getZ() * invW;

        Point3D worldOverW = v.getWorldPosition().mul(invW);

        Vec3D normalOverW = v.getNormal().mul(invW);

        Vec2D uvOverW = v.getUV().mul(invW);

        return new RasterVertex(
                pos,
                invW,
                zOverW,
                worldOverW,
                normalOverW,
                uvOverW);
    }
}