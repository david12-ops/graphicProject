package com.example.utils;

import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class RasterVertexBuilder {

    public static RasterVertex from(Vertex v) {

        double invW = 1.0 / v.getClipW();

        Vec3D pos = v.getPosition().ignoreW();

        double zOverW = v.getClipZ() * invW;

        Point3D worldOverW = v.getWorldPosition().mul(invW);

        Vec3D normalOverW = v.getNormal().mul(invW);

        Vec2D uvOverW = v.getUV().mul(invW);

        Col colorOverW = v.getColor().mul(invW);

        return new RasterVertex(
                pos,
                invW,
                zOverW,
                worldOverW,
                normalOverW,
                uvOverW,
                colorOverW);
    }
}