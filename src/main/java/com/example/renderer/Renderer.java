package com.example.renderer;

import java.util.List;
import java.util.Optional;

import com.example.enums.SolidState;
import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.model.solid.Solid;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.TriangleRasterizer;
import com.example.shader.Shader;
import com.example.shader.ShaderConstant;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4Scale;
import com.example.transforms.Mat4Transl;
import com.example.transforms.Point3D;
import com.example.transforms.Vec3D;

public class Renderer {
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;
    private Shader shader;
    private int width, height;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer, int width,
            int height, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.shader = new ShaderConstant();
        this.width = width;
        this.height = height;
        this.view = view;
        this.proj = proj;
    }

    public void render(Solid solid) {
        for (Part part : solid.getPartBuffer()) {
            switch (part.getTopologyType()) {
                case TopologyType.LINES:
                    Vec3D centerVec3d = getCenterVec(solid.getVertexBuffer());
                    int index = part.getStartIndex();

                    for (int i = 0; i < part.getCount(); i += 2) {
                        int indexA = solid.getIndexBuffer().get(i);
                        int indexB = solid.getIndexBuffer().get(i + 1);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);

                        vecA = scaleAroundCenter(solid.getState(), centerVec3d, vecA);
                        vecB = scaleAroundCenter(solid.getState(), centerVec3d, vecB);

                        if (solid.useModelMatrix()) {
                            // Modeling transformation (model) = model space -> world space
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            vecA = new Vertex(vecA.getPosition()
                                    .mul(solid.getModel()).mul(view).mul(proj), vecA.getColor());
                            vecB = new Vertex(vecB.getPosition()
                                    .mul(solid.getModel()).mul(view).mul(proj), vecB.getColor());
                        } else {
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            vecA = new Vertex(vecA.getPosition().mul(view).mul(proj), vecA.getColor());
                            vecB = new Vertex(vecB.getPosition().mul(view).mul(proj), vecB.getColor());
                        }

                        // Crop in clip space
                        if (!insideClipVolume(vecA) && !insideClipVolume(vecB))
                            continue;

                        double invW1 = 1.0 / vecA.getPosition().getW();
                        double invW2 = 1.0 / vecB.getPosition().getW();

                        double zOverW1 = vecA.getPosition().getZ() * invW1;
                        double zOverW2 = vecB.getPosition().getZ() * invW2;

                        Optional<Vec3D> dehomogA = vecA.getPosition().dehomog();
                        Optional<Vec3D> dehomogB = vecB.getPosition().dehomog();

                        // Dehomogenization
                        if (dehomogA.isEmpty() || dehomogB.isEmpty())
                            continue;

                        // Transform to screen window = NDC -> screen space
                        Vec3D vecA3D = transformToWindow(dehomogA.get());
                        Vec3D vecB3D = transformToWindow(dehomogB.get());

                        lineRasterizer.rasterize(
                                new Vertex(new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ()), vecA.getColor()),
                                invW1,
                                zOverW1,
                                new Vertex(new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ()), vecB.getColor()),
                                invW2,
                                zOverW2,
                                shader);
                    }
                    break;
                case TopologyType.TRIANGLES:
                    centerVec3d = getCenterVec(solid.getVertexBuffer());
                    index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i += 3) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);
                        Vertex vecC = solid.getVertexBuffer().get(indexC);

                        vecA = scaleAroundCenter(solid.getState(), centerVec3d, vecA);
                        vecB = scaleAroundCenter(solid.getState(), centerVec3d, vecB);
                        vecC = scaleAroundCenter(solid.getState(), centerVec3d, vecC);

                        if (solid.useModelMatrix()) {
                            // Modeling transformation (model) = model space -> world space
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            vecA = new Vertex(vecA.getPosition()
                                    .mul(solid.getModel()).mul(view).mul(proj), vecA.getColor());
                            vecB = new Vertex(vecB.getPosition()
                                    .mul(solid.getModel()).mul(view).mul(proj), vecB.getColor());
                            vecC = new Vertex(vecC.getPosition()
                                    .mul(solid.getModel()).mul(view).mul(proj), vecC.getColor());

                        } else {
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            vecA = new Vertex(vecA.getPosition().mul(view).mul(proj), vecA.getColor());
                            vecB = new Vertex(vecB.getPosition().mul(view).mul(proj), vecB.getColor());
                            vecC = new Vertex(vecC.getPosition().mul(view).mul(proj), vecC.getColor());
                        }

                        // Crop in clip space
                        if (!insideClipVolume(vecA) && !insideClipVolume(vecB)
                                && !insideClipVolume(vecC))
                            continue;

                        // 2. ořezání podle z TODO
                        float zMin = 0;
                        if (vecA.getZ() < zMin)
                            continue;

                        if (vecB.getZ() < zMin) {
                            // spočítám nový trojúhelník a budu ho rasterizovat
                        }

                        if (vecC.getZ() < zMin) {
                            // vzniknou dva nové trojúhelníky
                        }

                        Optional<Vec3D> dehomogA = vecA.getPosition().dehomog();
                        Optional<Vec3D> dehomogB = vecB.getPosition().dehomog();
                        Optional<Vec3D> dehomogC = vecC.getPosition().dehomog();

                        // Dehomogenization
                        if (dehomogA.isEmpty() || dehomogB.isEmpty() || dehomogC.isEmpty())
                            continue;

                        // Transform to screen window = NDC -> screen space
                        Vec3D vecA3D = transformToWindow(dehomogA.get());
                        Vec3D vecB3D = transformToWindow(dehomogB.get());
                        Vec3D vecC3D = transformToWindow(dehomogC.get());

                        triangleRasterizer.rasterize(
                                new Vertex(new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ()), vecA.getColor()),
                                new Vertex(new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ()), vecB.getColor()),
                                new Vertex(new Point3D(vecC3D.getX(), vecC3D.getY(), vecC3D.getZ()), vecC.getColor()),
                                shader);
                    }
                    break;
                case TopologyType.POINTS:
                    centerVec3d = getCenterVec(solid.getVertexBuffer());

                    for (int i = 0; i < part.getCount(); i++) {
                        index = solid.getIndexBuffer().get(part.getStartIndex() + i);
                        Vertex vertex = solid.getVertexBuffer().get(index);

                        vertex = scaleAroundCenter(
                                solid.getState(),
                                centerVec3d,
                                vertex);

                        if (solid.useModelMatrix()) {
                            vertex = new Vertex(vertex.getPosition().mul(solid.getModel())
                                    .mul(view)
                                    .mul(proj), vertex.getColor());
                        } else {
                            vertex = new Vertex(vertex.getPosition()
                                    .mul(view)
                                    .mul(proj), vertex.getColor());
                        }

                        if (!insideClipVolume(vertex))
                            continue;

                        Optional<Vec3D> dehomogA = vertex.getPosition().dehomog();

                        if (dehomogA.isEmpty())
                            continue;

                        Vec3D vec3D = transformToWindow(dehomogA.get());

                        lineRasterizer.rasterize(new Vertex(vec3D.getX(), vec3D.getY(), vec3D.getZ()), shader);
                    }
                    break;
            }
        }
    }

    private Vec3D getCenterVec(List<Vertex> vertexes) {
        double x = 0;
        double y = 0;
        double z = 0;

        for (Vertex v : vertexes) {
            x += v.getX();
            y += v.getY();
            z += v.getZ();
        }

        return new Vec3D(x / vertexes.size(), y / vertexes.size(), z / vertexes.size());
    }

    private Vertex scaleAroundCenter(
            SolidState solidState,
            Vec3D center,
            Vertex vertex) {

        if (solidState == SolidState.SELECTED) {
            vertex = new Vertex(vertex.getPosition()
                    .mul(new Mat4Transl(center.opposite()))
                    .mul(new Mat4Scale(1.2))
                    .mul(new Mat4Transl(center)), vertex.getColor());
        }

        return vertex;
    }

    private boolean insideClipVolume(Vertex v) {
        Point3D point3d = v.getPosition();
        double w = point3d.getW();

        return point3d.getX() >= -w && point3d.getX() <= w &&
                point3d.getY() >= -w && point3d.getY() <= w && point3d.getZ() >= 0
                && point3d.getZ() <= w;
    }

    private Vec3D transformToWindow(Vec3D v) {
        return v.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((width - 1) / 2., (height - 1) / 2., 1));
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }
}
