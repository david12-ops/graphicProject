package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
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
        addIndices(0, 1, 1, 2, 2, 3, 0, 3, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7);

        partBuffer.add(new Part(TopologyType.LINES, 0, 24));
    }
}
