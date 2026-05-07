package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class AxisY extends Solid {
    public AxisY() {
        // Fill in vb
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(0, 1, 0)));

        // Fill in ib
        addIndices(0, 1);

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
    }
}
