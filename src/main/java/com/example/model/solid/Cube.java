package com.example.model.solid;

import com.example.transforms.Point3D;

public class Cube extends Solid {
    public Cube(double size) {
        computePolygons();
    }

    private void computePolygons() {

        for (int i = 0; i < this.ib.size() - 1; i += 2) {
            int indexA = this.ib.get(i);
            int indexB = this.ib.get(i + 1);

            Point3D pointA = this.vb.get(indexA);
            Point3D pointB = this.vb.get(indexB);
        }
    }
}
