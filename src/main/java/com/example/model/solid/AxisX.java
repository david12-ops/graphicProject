package com.example.model.solid;

import com.example.transforms.Point3D;

public class AxisX extends Solid {
    public AxisX() {
        // Fill in vb
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(1, 0, 0));

        // Fill in ib
        ib.add(0);
        ib.add(1);
    }
}
