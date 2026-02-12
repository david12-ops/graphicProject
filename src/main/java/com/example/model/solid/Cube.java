package com.example.model.solid;

import com.example.transforms.Point3D;

public class Cube extends Solid {
    public Cube(double size) {
        double halfSize = size / 2.0;

        // Fill in vb
        // Bottom
        vb.add(new Point3D(-halfSize, -halfSize, -halfSize));
        vb.add(new Point3D(halfSize, -halfSize, -halfSize));
        vb.add(new Point3D(halfSize, halfSize, -halfSize));
        vb.add(new Point3D(-halfSize, halfSize, -halfSize));

        // Top
        vb.add(new Point3D(-halfSize, -halfSize, halfSize));
        vb.add(new Point3D(halfSize, -halfSize, halfSize));
        vb.add(new Point3D(halfSize, halfSize, halfSize));
        vb.add(new Point3D(-halfSize, halfSize, halfSize));

        // Fill in ib
        // Bottom
        ib.add(0);
        ib.add(1);
        ib.add(1);
        ib.add(2);
        ib.add(2);
        ib.add(3);
        ib.add(0);
        ib.add(3);

        // Top
        ib.add(4);
        ib.add(5);
        ib.add(5);
        ib.add(6);
        ib.add(6);
        ib.add(7);
        ib.add(7);
        ib.add(4);

        // Vertical
        ib.add(0);
        ib.add(4);
        ib.add(1);
        ib.add(5);
        ib.add(2);
        ib.add(6);
        ib.add(3);
        ib.add(7);
    }
}
