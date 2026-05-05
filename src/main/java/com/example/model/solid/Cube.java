package com.example.model.solid;

import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class Cube extends Solid {
    public Cube(double size) {
        double halfSize = size / 2.0;

        // Fill in vb
        // Bottom
        vertexBuffer.add(new Vertex(new Point3D(-halfSize, -halfSize, -halfSize)));
        vertexBuffer.add(new Vertex(new Point3D(halfSize, -halfSize, -halfSize)));
        vertexBuffer.add(new Vertex(new Point3D(halfSize, halfSize, -halfSize)));
        vertexBuffer.add(new Vertex(new Point3D(-halfSize, halfSize, -halfSize)));

        // Top
        vertexBuffer.add(new Vertex(new Point3D(-halfSize, -halfSize, halfSize)));
        vertexBuffer.add(new Vertex(new Point3D(halfSize, -halfSize, halfSize)));
        vertexBuffer.add(new Vertex(new Point3D(halfSize, halfSize, halfSize)));
        vertexBuffer.add(new Vertex(new Point3D(-halfSize, halfSize, halfSize)));

        // Fill in ib
        // Bottom
        indexBuffer.add(0);
        indexBuffer.add(1);
        indexBuffer.add(1);
        indexBuffer.add(2);
        indexBuffer.add(2);
        indexBuffer.add(3);
        indexBuffer.add(0);
        indexBuffer.add(3);

        // Top
        indexBuffer.add(4);
        indexBuffer.add(5);
        indexBuffer.add(5);
        indexBuffer.add(6);
        indexBuffer.add(6);
        indexBuffer.add(7);
        indexBuffer.add(7);
        indexBuffer.add(4);

        // Vertical
        indexBuffer.add(0);
        indexBuffer.add(4);
        indexBuffer.add(1);
        indexBuffer.add(5);
        indexBuffer.add(2);
        indexBuffer.add(6);
        indexBuffer.add(3);
        indexBuffer.add(7);
    }
}
