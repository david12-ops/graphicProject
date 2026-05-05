package com.example.model.solid;

import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class AxisX extends Solid {
    public AxisX() {
        // Fill in vb
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(1, 0, 0)));

        // Fill in ib
        indexBuffer.add(0);
        indexBuffer.add(1);
    }
}
