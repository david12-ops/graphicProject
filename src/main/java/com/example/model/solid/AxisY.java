package com.example.model.solid;

import com.example.transforms.Col;
import com.example.transforms.Point3D;

public class AxisY extends Solid {
    public AxisY() {
        color = new Col(0x00ff00);

        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 1, 0));

        ib.add(0);
        ib.add(1);
    }
}
