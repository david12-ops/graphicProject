package com.example.model.solid;

import com.example.transforms.Point3D;

public class AxisY extends Solid {
    public AxisY() {
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 1, 0));

        ib.add(0);
        ib.add(1);
    }
}
