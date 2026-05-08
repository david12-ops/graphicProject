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
        // addIndices(0, 1, 1, 2, 2, 3, 0, 3, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6,
        // 3, 7);

        // Bottom
        addIndices(0, 1, 2);
        addIndices(0, 2, 3);

        // Top
        addIndices(4, 6, 5);
        addIndices(4, 7, 6);

        // Front
        addIndices(0, 4, 5);
        addIndices(0, 5, 1);

        // Back
        addIndices(3, 2, 6);
        addIndices(3, 6, 7);

        // Left
        addIndices(0, 3, 7);
        addIndices(0, 7, 4);

        // Right
        addIndices(1, 5, 6);
        addIndices(1, 6, 2);

        // partBuffer.add(new Part(TopologyType.LINES, 0, 24));
        partBuffer.add(new Part(
                TopologyType.TRIANGLES,
                0,
                36));
    }
}
