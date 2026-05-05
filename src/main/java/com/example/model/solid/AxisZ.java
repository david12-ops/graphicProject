package com.example.model.solid;

import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class AxisZ extends Solid {
    public AxisZ() {
        // Fill in vertexBuffer
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 1)));

        // Fill in ib
        indexBuffer.add(0);
        indexBuffer.add(1);
    }
}
