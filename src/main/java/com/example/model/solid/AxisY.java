package com.example.model.solid;

import com.example.enums.SolidModel;
import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

public class AxisY extends Solid {
        public AxisY(Col[] colors, SolidModel solidModel) {
                if (solidModel == SolidModel.SOLID) {
                        initialFillMesh(colors);
                } else
                        initialWireFrameMesh(colors);
        }

        private void initialWireFrameMesh(Col[] colors) {
                vertexBuffer.clear();
                indexBuffer.clear();
                partBuffer.clear();

                // Fill in vertexBuffer
                vertexBuffer.add(new Vertex(
                                new Point3D(0, 0, 0),
                                colors[0]));

                vertexBuffer.add(new Vertex(
                                new Point3D(0, 0.85, 0),
                                colors[1]));

                vertexBuffer.add(new Vertex(
                                new Point3D(-0.05, 0.85, 0),
                                colors[0]));

                vertexBuffer.add(new Vertex(
                                new Point3D(0.00, 1.15, 0),
                                colors[1]));

                vertexBuffer.add(new Vertex(
                                new Point3D(0.05, 0.85, 0),
                                colors[2]));

                // Line shaft
                addIndices(0, 1);

                // Arrow head
                addIndices(2, 3);
                addIndices(3, 4);
                addIndices(4, 2);

                partBuffer.add(new Part(
                                TopologyType.LINES,
                                0,
                                8));
        }

        private void initialFillMesh(Col[] colors) {
                vertexBuffer.clear();
                indexBuffer.clear();
                partBuffer.clear();

                Vec3D normal = new Vec3D(0, 0, 1);

                // Fill in vertexBuffer
                vertexBuffer.add(new Vertex(
                                new Point3D(0, 0, 0),
                                colors[0], null, normal));

                vertexBuffer.add(new Vertex(
                                new Point3D(0, 0.85, 0),
                                colors[1], null, normal));

                vertexBuffer.add(new Vertex(
                                new Point3D(-0.05, 0.85, 0),
                                colors[0],
                                new Vec2D(0, 0),
                                normal));

                vertexBuffer.add(new Vertex(
                                new Point3D(0.00, 1.15, 0),
                                colors[1],
                                new Vec2D(0.5, 1),
                                normal));

                vertexBuffer.add(new Vertex(
                                new Point3D(0.05, 0.85, 0),
                                colors[2],
                                new Vec2D(1, 0),
                                normal));

                // Fill in index buffer
                addIndices(0, 1);

                addIndices(2, 3, 4);

                partBuffer.add(new Part(
                                TopologyType.LINES,
                                0,
                                2));

                partBuffer.add(new Part(
                                TopologyType.TRIANGLES,
                                2,
                                3));
        }
}
