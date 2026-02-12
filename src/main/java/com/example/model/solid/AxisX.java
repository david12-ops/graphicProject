package com.example.model.solid;

import com.example.transforms.Point3D;

public class AxisX extends Solid {
    public AxisX() {
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(1, 0, 0));

        ib.add(0);
        ib.add(1);
    }
}
