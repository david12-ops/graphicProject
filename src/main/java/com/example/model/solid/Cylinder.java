package com.example.model.solid;

import com.example.transforms.Point3D;

public class Cylinder extends Solid {
    public Cylinder(double radius, double height, int segments) {
        int[] bottomVb = new int[segments];
        int[] topVb = new int[segments];
        double halfHeight = height / 2;

        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);

            bottomVb[i] = vb.size();
            vb.add(new Point3D(x, y, -halfHeight));

            topVb[i] = vb.size();
            vb.add(new Point3D(x, y, halfHeight));
        }

        for (int i = 0; i < segments; i++) {

            int next = (i + 1) % segments;

            // dolní kruh
            ib.add(bottomVb[i]);
            ib.add(bottomVb[next]);

            // horní kruh
            ib.add(topVb[i]);
            ib.add(topVb[next]);

            // svislé hrany
            ib.add(bottomVb[i]);
            ib.add(topVb[i]);
        }
    }
}
