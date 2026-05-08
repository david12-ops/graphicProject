package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class Round extends Solid {
    public Round(Point3D center, double radius, int segments) {
        vertexBuffer.add(new Vertex(center));

        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;

            vertexBuffer.add(new Vertex(
                    new Point3D(
                            center.getX() + radius * Math.cos(angle),
                            center.getY() + radius * Math.sin(angle),
                            center.getZ())));
        }

        // Triangle
        for (int i = 1; i <= segments; i++) {
            addIndices(0, i, ((i % segments) + 1));
        }

        partBuffer.add(
                new Part(
                        TopologyType.TRIANGLES,
                        0,
                        3 * segments));
    }
}
