package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Cubic;
import com.example.transforms.Point3D;

public class BezierCurve extends Solid {

    private int segments;
    private Cubic cubicCoons;

    public BezierCurve(Point3D[] points, int segments) {
        this.segments = segments;
        this.cubicCoons = new Cubic(Cubic.BEZIER, points);
    }

    /**
     * Computes the Bézier cubic curve geometry.
     *
     * The method first clears existing vertex and index buffers.
     * Then it uniformly samples the parametric interval <0,1>
     * using the specified number of segments.
     *
     * After all vertices are generated, the index buffer (ib)
     * is filled with consecutive index pairs so the curve can
     * be rendered as a connected polyline.
     *
     * Result:
     * - vb contains (segments + 1) sampled points
     * - ib defines line connectivity between adjacent points
     */
    public void compute() {
        clear();

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments; // normalized curve parameter (0 = start, 1 = end)
            Point3D point3d = cubicCoons.compute(t);

            this.vertexBuffer.add(new Vertex(point3d));
        }

        for (int i = 0; i < vertexBuffer.size() - 1; i++) {
            addIndices(i, i + 1);
        }

        partBuffer.add(new Part(TopologyType.LINES, 0, 2 * segments));
    }

    private void clear() {
        vertexBuffer.clear();
        indexBuffer.clear();
    }
}
