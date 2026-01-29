package com.example.model.solid;

import com.example.transforms.Point3D;

public class Arrow extends Solid {
    public Arrow() {
        // Naplním vb
        vb.add(new Point3D(0, 0, 0)); // v0
        vb.add(new Point3D(0.8, 0, 0)); // v1
        vb.add(new Point3D(0.8, 0, -0.2)); // v2
        vb.add(new Point3D(1, 0, 0)); // v3
        vb.add(new Point3D(0.8, 0, 0.2)); // v4

        // Naplním ib
        ib.add(0);
        ib.add(1);

        ib.add(2);
        ib.add(3);

        ib.add(4);
        ib.add(2);

        ib.add(3);
        ib.add(4);
    }

}
