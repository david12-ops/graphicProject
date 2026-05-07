package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class AxisZ extends Solid {
    public AxisZ() {
        // Fill in vertexBuffer
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 1)));

        // Fill in ib
        addIndices(0, 1);

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
    }
}
