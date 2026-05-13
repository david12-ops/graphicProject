package com.example.model.solid;

import com.example.enums.SolidModel;
import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

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
    public Cylinder(double radius, double height, int segments, Col[] colors, SolidModel solidModel) {
        if (solidModel == SolidModel.SOLID) {
            initialFillMesh(radius, height, segments, colors);
        } else
            initialWireFrameMesh(radius, height, segments, colors);
    }

    private void initialWireFrameMesh(double radius, double height, int segments, Col[] colors) {
        vertexBuffer.clear();
        indexBuffer.clear();
        partBuffer.clear();

        int[] bottomVb = new int[segments];
        int[] topVb = new int[segments];
        double halfHeight = height / 2;

        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);

            bottomVb[i] = vertexBuffer.size();
            vertexBuffer.add(new Vertex(new Point3D(x, y, -halfHeight), colors[0]));

            topVb[i] = vertexBuffer.size();
            vertexBuffer.add(new Vertex(new Point3D(x, y, halfHeight), colors[2]));
        }

        for (int i = 0; i < segments; i++) {

            int next = (i + 1) % segments;

            addIndices(bottomVb[i], bottomVb[next], topVb[i], topVb[next], bottomVb[i], topVb[i]);
        }

        partBuffer.add(new Part(TopologyType.LINES, 0, 6 * segments));
    }

    private void initialFillMesh(double radius, double height, int segments, Col[] colors) {
        vertexBuffer.clear();
        indexBuffer.clear();
        partBuffer.clear();

        int[] bottomVb = new int[segments + 1];
        int[] topVb = new int[segments + 1];
        double halfHeight = height / 2;

        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            Vec3D normal = new Vec3D(x, y, 0).normalized().orElse(new Vec3D(0, 0, 1));
            double u = (double) i / segments;

            bottomVb[i] = vertexBuffer.size();
            vertexBuffer.add(
                    new Vertex(new Point3D(x, y, -halfHeight), colors[0], new Vec2D(u, 0), normal));

            topVb[i] = vertexBuffer.size();
            vertexBuffer.add(
                    new Vertex(new Point3D(x, y, halfHeight), colors[2], new Vec2D(u, 1), normal));
        }

        for (int i = 0; i < segments; i++) {

            int next = i + 1;

            // First triangle
            addIndices(
                    bottomVb[i],
                    bottomVb[next],
                    topVb[i]);

            // Second triangle
            addIndices(
                    topVb[i],
                    bottomVb[next],
                    topVb[next]);
        }

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 6 * segments));
    }
}
