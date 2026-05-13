package com.example.model.solid;

import com.example.enums.SolidModel;
import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class Cube extends Solid {
        public Cube(double size, Col[] colors, SolidModel solidModel) {
                double halfSize = size / 2.0;

                if (solidModel == SolidModel.SOLID) {
                        initialFillMesh(halfSize, colors);
                } else
                        initialWireFrameMesh(halfSize, colors);
        }

        private void initialWireFrameMesh(double halfSize, Col[] colors) {
                vertexBuffer.clear();
                indexBuffer.clear();
                partBuffer.clear();

                // Fill in vb
                // Bottom
                vertexBuffer.add(new Vertex(new Point3D(-halfSize, -halfSize, -halfSize),
                                colors[0]));
                vertexBuffer.add(new Vertex(new Point3D(halfSize, -halfSize, -halfSize),
                                colors[1]));
                vertexBuffer.add(new Vertex(new Point3D(halfSize, halfSize, -halfSize),
                                colors[2]));
                vertexBuffer.add(new Vertex(new Point3D(-halfSize, halfSize, -halfSize), new Col(255, 255, 255)));

                // Top
                vertexBuffer.add(new Vertex(new Point3D(-halfSize, -halfSize, halfSize),
                                colors[0]));
                vertexBuffer.add(new Vertex(new Point3D(halfSize, -halfSize, halfSize),
                                colors[1]));
                vertexBuffer.add(new Vertex(new Point3D(halfSize, halfSize, halfSize),
                                colors[2]));
                vertexBuffer.add(new Vertex(new Point3D(-halfSize, halfSize, halfSize), new Col(255, 255, 255)));

                // Fill in ib
                addIndices(0, 1, 1, 2, 2, 3, 0, 3, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6,
                                3, 7);

                partBuffer.add(new Part(
                                TopologyType.LINES,
                                0,
                                24));
        }

        private void initialFillMesh(double halfSize, Col[] colors) {
                vertexBuffer.clear();
                indexBuffer.clear();
                partBuffer.clear();

                // Fill in vb
                // Bottom
                Vec3D bottomNormal = new Vec3D(0, 0, -1);
                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, -halfSize, -halfSize),
                                colors[0],
                                new Vec2D(0, 0), bottomNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, -halfSize, -halfSize),
                                new Col(255, 255, 255),
                                new Vec2D(1, 0), bottomNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, halfSize, -halfSize),
                                colors[1],
                                new Vec2D(1, 1), bottomNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, halfSize, -halfSize),
                                colors[2],
                                new Vec2D(0, 1), bottomNormal));

                // Top
                Vec3D topNormal = new Vec3D(0, 0, 1);
                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, -halfSize, halfSize),
                                colors[0],
                                new Vec2D(0, 0), topNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, -halfSize, halfSize),
                                new Col(255, 255, 255),
                                new Vec2D(1, 0), topNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, halfSize, halfSize),
                                colors[1],
                                new Vec2D(1, 1), topNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, halfSize, halfSize),
                                colors[2],
                                new Vec2D(0, 1), topNormal));

                // front
                Vec3D frontNormal = new Vec3D(0, -1, 0);
                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, -halfSize, -halfSize),
                                colors[0],
                                new Vec2D(0, 0), frontNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, -halfSize, -halfSize),
                                new Col(255, 255, 255),
                                new Vec2D(1, 0), frontNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, -halfSize, halfSize),
                                colors[1],
                                new Vec2D(1, 1), frontNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, -halfSize, halfSize),
                                colors[2],
                                new Vec2D(0, 1), frontNormal));

                // back
                Vec3D backNormal = new Vec3D(0, 1, 0);
                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, halfSize, -halfSize),
                                colors[0],
                                new Vec2D(0, 0), backNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, halfSize, -halfSize),
                                new Col(255, 255, 255),
                                new Vec2D(1, 0), backNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, halfSize, halfSize),
                                colors[1],
                                new Vec2D(1, 1), backNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, halfSize, halfSize),
                                colors[2],
                                new Vec2D(0, 1), backNormal));

                // left
                Vec3D leftNormal = new Vec3D(-1, 0, 0);
                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, -halfSize, -halfSize),
                                colors[0],
                                new Vec2D(0, 0), leftNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, halfSize, -halfSize),
                                new Col(255, 255, 255),
                                new Vec2D(1, 0), leftNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, halfSize, halfSize),
                                colors[1],
                                new Vec2D(1, 1), leftNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-halfSize, -halfSize, halfSize),
                                colors[2],
                                new Vec2D(0, 1), leftNormal));

                // right
                Vec3D rightNormal = new Vec3D(1, 0, 0);
                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, -halfSize, -halfSize),
                                colors[0],
                                new Vec2D(0, 0), rightNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, halfSize, -halfSize),
                                new Col(255, 255, 255),
                                new Vec2D(1, 0), rightNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, halfSize, halfSize),
                                colors[1],
                                new Vec2D(1, 1), rightNormal));

                vertexBuffer.add(new Vertex(
                                new Point3D(halfSize, -halfSize, halfSize),
                                colors[2],
                                new Vec2D(0, 1), rightNormal));

                // Bottom
                addIndices(0, 1, 2);
                addIndices(0, 2, 3);

                // Top
                addIndices(4, 6, 5);
                addIndices(4, 7, 6);

                // Front
                addIndices(8, 9, 10);
                addIndices(8, 10, 11);

                // Back
                addIndices(12, 14, 13);
                addIndices(12, 15, 14);

                // Left
                addIndices(16, 17, 18);
                addIndices(16, 18, 19);

                // Right
                addIndices(20, 22, 21);
                addIndices(20, 23, 22);

                partBuffer.add(new Part(
                                TopologyType.TRIANGLES,
                                0,
                                36));
        }
}
