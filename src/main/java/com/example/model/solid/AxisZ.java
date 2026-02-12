package com.example.model.solid;

import com.example.transforms.Point3D;

public class AxisZ extends Solid {
    public AxisZ() {
        // Fill in vb
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 0, 1));

        // Fill in ib
        ib.add(0);
        ib.add(1);
    }
}
