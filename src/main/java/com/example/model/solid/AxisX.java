package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class AxisX extends Solid {
    public AxisX() {
        // Fill in vb
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(1, 0, 0)));

        vertexBuffer.add(new Vertex(new Point3D(0.85, -0.05, 0)));
        vertexBuffer.add(new Vertex(new Point3D(1.15, 0.00, 0)));
        vertexBuffer.add(new Vertex(new Point3D(0.85, 0.05, 0)));

        // Fill in ib
        addIndices(0, 1);

        addIndices(2, 3, 4);

        partBuffer.add(new Part(TopologyType.LINES, 0, 2));
        partBuffer.add(
                new Part(
                        TopologyType.TRIANGLES,
                        2,
                        3));
    }
}
