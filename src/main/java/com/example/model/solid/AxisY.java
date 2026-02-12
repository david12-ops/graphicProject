package com.example.model.solid;

import com.example.transforms.Point3D;

public class AxisY extends Solid {
    public AxisY() {
        // Fill in vb
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 1, 0));

        // Fill in ib
        ib.add(0);
        ib.add(1);
    }
}
