package com.example.model.solid;

import com.example.transforms.Cubic;
import com.example.transforms.Point3D;

public class FergusonCurve extends Solid {

    private int segments;
    private Cubic cubicCoons;

    public FergusonCurve(Point3D[] points, int segments) {
        this.segments = segments;
        this.cubicCoons = new Cubic(Cubic.FERGUSON, points);
    }

    public void compute() {
        for (int i = 0; i <= segments; i++) {
            double distance = (double) i / segments;
            Point3D point3d = cubicCoons.compute(distance);

            this.vb.add(point3d);
        }

        for (int i = 0; i < vb.size() - 1; i++) {
            ib.add(i);
            ib.add(i + 1);
        }
    }
}
