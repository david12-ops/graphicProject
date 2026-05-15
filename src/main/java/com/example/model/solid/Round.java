package com.example.model.solid;

import com.example.enums.SolidModel;
import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class Round extends Solid {
    public Round(Point3D center, double radius, int stacks, int slices, Col[] colors, SolidModel solidModel) {
        if (solidModel == SolidModel.SOLID) {

            initialFillMesh(center, radius, stacks, slices, colors);

        } else
            initialWireFrameMesh(center, radius, stacks, slices, colors);
    }

    /**
     * Initializes a wireframe sphere mesh.
     *
     * <p>
     * The sphere is generated using two sets of circular segments:
     * </p>
     *
     * <ul>
     * <li>Latitude rings (horizontal circles)</li>
     * <li>Longitude rings (vertical circles)</li>
     * </ul>
     *
     * <p>
     * Each segment is stored as a line primitive.
     * The generated geometry is added into:
     * </p>
     *
     * <ul>
     * <li>{@code vertexBuffer}</li>
     * <li>{@code indexBuffer}</li>
     * <li>{@code partBuffer}</li>
     * </ul>
     *
     * <p>
     * Sphere parameterization:
     * </p>
     *
     * :contentReference[oaicite:0]{index=0}
     *
     * <p>
     * Latitude angle:
     * </p>
     *
     * :contentReference[oaicite:1]{index=1}
     *
     * <p>
     * Longitude angle:
     * </p>
     *
     * :contentReference[oaicite:2]{index=2}
     *
     * @param center sphere center position
     * @param radius sphere radius
     * @param stacks number of horizontal subdivisions
     * @param slices number of vertical subdivisions
     * @param colors color palette used for generated vertices
     */
    private void initialWireFrameMesh(Point3D center, double radius, int stacks, int slices, Col[] colors) {
        vertexBuffer.clear();
        indexBuffer.clear();
        partBuffer.clear();

        // Latitude rings
        for (int i = 1; i < stacks; i++) {

            double theta = Math.PI * i / stacks;
            double y = radius * Math.cos(theta);
            double ringRadius = radius * Math.sin(theta);

            for (int j = 0; j < slices; j++) {
                double phi1 = 2 * Math.PI * j / slices;
                double phi2 = 2 * Math.PI * (j + 1) / slices;

                Point3D p1 = new Point3D(
                        center.getX() + ringRadius * Math.cos(phi1),
                        center.getY() + y,
                        center.getZ() + ringRadius * Math.sin(phi1));

                Point3D p2 = new Point3D(
                        center.getX() + ringRadius * Math.cos(phi2),
                        center.getY() + y,
                        center.getZ() + ringRadius * Math.sin(phi2));

                int index = vertexBuffer.size();
                vertexBuffer.add(new Vertex(p1, colors[i % colors.length]));
                vertexBuffer.add(new Vertex(p2, colors[(i + 1) % colors.length]));

                addIndices(index, index + 1);
            }
        }

        // Longitude rings
        for (int j = 0; j < slices; j++) {
            double phi = 2 * Math.PI * j / slices;

            for (int i = 0; i < stacks; i++) {
                double theta1 = Math.PI * i / stacks;
                double theta2 = Math.PI * (i + 1) / stacks;

                Point3D p1 = new Point3D(
                        center.getX() + radius * Math.sin(theta1) * Math.cos(phi),
                        center.getY() + radius * Math.cos(theta1),
                        center.getZ() + radius * Math.sin(theta1) * Math.sin(phi));

                Point3D p2 = new Point3D(
                        center.getX() + radius * Math.sin(theta2) * Math.cos(phi),
                        center.getY() + radius * Math.cos(theta2),
                        center.getZ() + radius * Math.sin(theta2) * Math.sin(phi));

                int index = vertexBuffer.size();
                vertexBuffer.add(new Vertex(p1, colors[i % colors.length]));
                vertexBuffer.add(new Vertex(p2, colors[(i + 1) % colors.length]));

                addIndices(index, index + 1);
            }
        }

        partBuffer.add(
                new Part(
                        TopologyType.LINES,
                        0,
                        indexBuffer.size()));
    }

    /**
     * Initializes a filled sphere mesh using triangle primitives.
     *
     * <p>
     * The sphere is generated using spherical coordinates and tessellated
     * into triangles.
     * </p>
     *
     * <p>
     * Generated per-vertex attributes:
     * </p>
     *
     * <ul>
     * <li>Position</li>
     * <li>Color</li>
     * <li>Texture coordinates (UV)</li>
     * <li>Normal vector</li>
     * </ul>
     *
     * <p>
     * Sphere parameterization:
     * </p>
     *
     * :contentReference[oaicite:3]{index=3}
     *
     * <p>
     * UV mapping:
     * </p>
     *
     * :contentReference[oaicite:4]{index=4}
     *
     * <p>
     * Surface normal for a sphere:
     * </p>
     *
     * :contentReference[oaicite:5]{index=5}
     *
     * <p>
     * Every rectangular patch between neighboring stacks and slices
     * is split into two triangles.
     * </p>
     *
     * @param center sphere center position
     * @param radius sphere radius
     * @param stacks number of horizontal subdivisions
     * @param slices number of vertical subdivisions
     * @param colors color palette used for generated vertices
     */
    private void initialFillMesh(Point3D center, double radius, int stacks, int slices, Col[] colors) {
        vertexBuffer.clear();
        indexBuffer.clear();
        partBuffer.clear();

        for (int i = 0; i <= stacks; i++) {

            double theta = Math.PI * i / stacks;

            double sinTheta = Math.sin(theta);
            double cosTheta = Math.cos(theta);

            for (int j = 0; j <= slices; j++) {

                double phi = 2 * Math.PI * j / slices;

                double sinPhi = Math.sin(phi);
                double cosPhi = Math.cos(phi);

                double nx = sinTheta * cosPhi;
                double ny = cosTheta;
                double nz = sinTheta * sinPhi;

                double x = center.getX() + radius * nx;
                double y = center.getY() + radius * ny;
                double z = center.getZ() + radius * nz;

                double u = j / (double) slices;
                double v = i / (double) stacks;

                vertexBuffer.add(
                        new Vertex(
                                new Point3D(x, y, z),
                                colors[(i + j) % colors.length],
                                new Vec2D(u, v),
                                new Vec3D(nx, ny, nz)));
            }
        }

        // Triangles
        for (int i = 0; i < stacks; i++) {

            for (int j = 0; j < slices; j++) {

                int first = i * (slices + 1) + j;
                int second = first + slices + 1;

                addIndices(first, second, first + 1);

                addIndices(second, second + 1, first + 1);
            }
        }

        partBuffer.add(
                new Part(
                        TopologyType.TRIANGLES,
                        0,
                        indexBuffer.size()));
    }
}
