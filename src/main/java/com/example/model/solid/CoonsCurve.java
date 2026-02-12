package com.example.model.solid;

import com.example.transforms.Cubic;
import com.example.transforms.Point3D;

public class CoonsCurve extends Solid {

    private int segments;
    private Cubic cubicCoons;

    public CoonsCurve(Point3D[] points, int segments) {
        this.segments = segments;
        this.cubicCoons = new Cubic(Cubic.COONS, points);
    }

    /**
     * Computes the Coons cubic curve geometry.
     *
     * The method clears existing vertex and index buffers,
     * then uniformly samples the parametric interval <0,1>
     * using the defined number of segments.
     *
     * After generating all vertices, the index buffer (ib)
     * is filled with consecutive index pairs so the curve
     * can be rendered as a connected polyline.
     *
     * Result:
     * - vb contains (segments + 1) sampled points
     * - ib defines line connectivity between adjacent vertices
     */
    public void compute() {
        clear();

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments; // normalized curve parameter (0 = start, 1 = end)
            Point3D point3d = cubicCoons.compute(t);

            this.vb.add(point3d);
        }

        for (int i = 0; i < vb.size() - 1; i++) {
            ib.add(i);
            ib.add(i + 1);
        }
    }

    private void clear() {
        vb.clear();
        ib.clear();
    }
}
