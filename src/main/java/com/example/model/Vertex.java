package com.example.model;

import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class Vertex implements Vectorazible<Vertex> {
    private final Point3D position;
    private final Col color;
    private Vec3D normal;
    private Vec2D uv;
    // další atributy: normála, uv, one

    public Vertex(Point3D position, Col color, Vec3D normal, Vec2D uv) {
        this.position = position;
        this.color = color;
        this.uv = uv;
        this.normal = normal;
    }

    public Vertex(Point3D position, Col color) {
        this.position = position;
        this.color = color;
    }

    public Vertex(Point3D position) {
        this(position, new Col(255, 255, 255)); // Default to white color
    }

    public Vertex(double x, double y, double z) {
        this.position = new Point3D(x, y, z);
        this.color = new Col(0xffffff);
    }

    public Vertex(double x, double y, double z, Col color) {
        this.position = new Point3D(x, y, z);
        this.color = color;
    }

    public Point3D getPosition() {
        return position;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getZ() {
        return position.getZ();
    }

    public Col getColor() {
        return color;
    }

    public Vec2D getUV() {
        return uv;
    }

    public void setUV(Vec2D uv) {
        this.uv = uv;
    }

    public Vec3D getNormal() {
        return normal;
    }

    public void setUV(Vec3D normal) {
        this.normal = normal;
    }

    @Override
    public Vertex mul(double d) {
        return new Vertex(position.mul(d), color.mul(color));
    }

    @Override
    public Vertex add(Vertex v) {
        return new Vertex(position.add(v.getPosition()), color.add(v.getColor()));
    }
}
