package com.example.model.solid;

import com.example.model.Vertex;
import com.example.transforms.Cubic;
import com.example.transforms.Point3D;

public class FergusonCurve extends Solid {

    private int segments;
    private Cubic cubicCoons;

    public FergusonCurve(Point3D[] points, int segments) {
        this.segments = segments;
        this.cubicCoons = new Cubic(Cubic.FERGUSON, points);
    }

    /**
     * Computes the Ferguson cubic curve geometry.
     *
     * The method clears existing vertex and index buffers,
     * then uniformly samples the parametric interval <0,1>
     * according to the specified number of segments.
     *
     * After generating all vertices, the index buffer (ib)
     * is filled with consecutive index pairs so the curve
     * can be rendered as a connected polyline.
     *
     * Result:
     * - vb contains (segments + 1) sampled curve points
     * - ib defines line connectivity between adjacent vertices
     */
    public void compute() {
        clear();

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments; // normalized curve parameter (0 = start, 1 = end)
            Point3D point3d = cubicCoons.compute(t);

            this.vertexBuffer.add(new Vertex(point3d));
        }

        for (int i = 0; i < vertexBuffer.size() - 1; i++) {
            indexBuffer.add(i);
            indexBuffer.add(i + 1);
        }
    }

    private void clear() {
        vertexBuffer.clear();
        indexBuffer.clear();
    }
}
