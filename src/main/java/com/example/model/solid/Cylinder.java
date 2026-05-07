package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class Cylinder extends Solid {

    /**
     * Creates a wireframe cylinder centered at the origin.
     *
     * The cylinder is generated along the Z-axis with its base at
     * -height/2 and its top at +height/2, so it is centered at (0, 0, 0).
     *
     * Vertices are created for both the bottom and top circles.
     * The index buffer defines:
     * - edges of the bottom circle,
     * - edges of the top circle,
     * - vertical edges connecting corresponding bottom and top vertices.
     *
     * The cylinder is represented as a set of line segments (wireframe),
     * not as a filled triangle mesh.
     *
     * @param radius   radius of the cylinder
     * @param height   total height of the cylinder
     * @param segments number of segments used to approximate the circular base
     *                 (must be >= 3 for a valid cylinder)
     */
    public Cylinder(double radius, double height, int segments) {
        int[] bottomVb = new int[segments];
        int[] topVb = new int[segments];
        double halfHeight = height / 2;

        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);

            bottomVb[i] = vertexBuffer.size();
            vertexBuffer.add(new Vertex(new Point3D(x, y, -halfHeight)));

            topVb[i] = vertexBuffer.size();
            vertexBuffer.add(new Vertex(new Point3D(x, y, halfHeight)));
        }

        for (int i = 0; i < segments; i++) {

            int next = (i + 1) % segments;

            addIndices(bottomVb[i], bottomVb[next], topVb[i], topVb[next], bottomVb[i], topVb[i]);
        }

        partBuffer.add(new Part(TopologyType.LINES, 0, 6 * segments));
    }
}
