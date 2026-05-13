package com.example.renderer;

import java.util.List;
import java.util.Optional;

import com.example.enums.SolidState;
import com.example.enums.TopologyType;
import com.example.model.Light;
import com.example.model.Part;
import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.model.solid.Solid;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.PointRasterizer;
import com.example.rasterize.TriangleRasterizer;
import com.example.shader.Shader;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4Scale;
import com.example.transforms.Mat4Transl;
import com.example.transforms.Point3D;
import com.example.transforms.Vec3D;
import com.example.utils.Clipper;
import com.example.utils.RasterVertexBuilder;

public class Renderer {
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;
    private PointRasterizer pointRasterizer;

    private int width, height;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer,
            PointRasterizer pointRasterizer, Light sceneLight, int width,
            int height, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.pointRasterizer = pointRasterizer;
        this.width = width;
        this.height = height;
        this.view = view;
        this.proj = proj;
    }

    public void render(Solid solid) {
        Vec3D centerVec3d = solid.getCenterVec3d();
        Mat4 finalMatrix = solid.useModelMatrix() ? solid.getModel().mul(view).mul(proj) : (view).mul(proj);

        if (solid.getState() == SolidState.SELECTED) {
            finalMatrix = new Mat4Transl(centerVec3d.opposite())
                    .mul(new Mat4Scale(1.2))
                    .mul(new Mat4Transl(centerVec3d))
                    .mul(finalMatrix);
        }

        for (Part part : solid.getPartBuffer()) {
            int index = part.getStartIndex();
            switch (part.getTopologyType()) {
                case TopologyType.LINES:
                    // barva
                    for (int i = 0; i < part.getCount(); i += 2) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);

                        vecA = new Vertex(vecA.getPosition().mul(finalMatrix), vecA.getColor(), vecA.getUV(),
                                vecA.getNormal());
                        vecB = new Vertex(vecB.getPosition().mul(finalMatrix), vecB.getColor(), vecB.getUV(),
                                vecB.getNormal());

                        // Crop in clip space
                        if (Clipper.clipReject(vecA, vecB))
                            continue;

                        Optional<Vertex[]> clipped = Clipper.clipByZ(vecA, vecB);

                        if (clipped.isEmpty())
                            continue;

                        vecA = clipped.get()[0];
                        vecB = clipped.get()[1];

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
                                new Vertex(new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ()), vecA.getColor(),
                                        vecA.getUV(), vecA.getNormal()),
                                invW1,
                                zOverW1,
                                new Vertex(new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ()), vecB.getColor(),
                                        vecB.getUV(), vecB.getNormal()),
                                invW2,
                                zOverW2,
                                solid.getShader());
                    }
                    break;
                case TopologyType.TRIANGLES:
                    Mat4 normalMatrix = solid.getModel();

                    Optional<Mat4> inverse = solid.getModel().inverse();

                    if (inverse.isPresent()) {
                        normalMatrix = inverse.get().transpose();
                    }

                    for (int i = 0; i < part.getCount(); i += 3) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);
                        Vertex vecC = solid.getVertexBuffer().get(indexC);

                        Point3D worldA = vecA.getPosition().mul(solid.getModel());
                        Point3D worldB = vecB.getPosition().mul(solid.getModel());
                        Point3D worldC = vecC.getPosition().mul(solid.getModel());

                        vecA = new Vertex(
                                vecA.getPosition().mul(finalMatrix),
                                vecA.getColor(),
                                vecA.getUV(),
                                computeNormalWithMtrix(vecA, normalMatrix));

                        vecB = new Vertex(
                                vecB.getPosition().mul(finalMatrix),
                                vecB.getColor(),
                                vecB.getUV(),
                                computeNormalWithMtrix(vecB, normalMatrix));

                        vecC = new Vertex(
                                vecC.getPosition().mul(finalMatrix),
                                vecC.getColor(),
                                vecC.getUV(),
                                computeNormalWithMtrix(vecC, normalMatrix));

                        vecA.setWorldPosition(worldA);
                        vecB.setWorldPosition(worldB);
                        vecC.setWorldPosition(worldC);

                        vecA.setClipW(vecA.getPosition().getW());
                        vecB.setClipW(vecB.getPosition().getW());
                        vecC.setClipW(vecC.getPosition().getW());

                        vecA.setClipZ(vecA.getPosition().getZ());
                        vecB.setClipZ(vecB.getPosition().getZ());
                        vecC.setClipZ(vecC.getPosition().getZ());

                        // Crop in clip space
                        if (Clipper.clipReject(vecA, vecB, vecC))
                            continue;

                        // 2. ořezání podle z TODO
                        List<Vertex> output = Clipper.clipByZ(List.of(vecA, vecB, vecC));

                        if (output.size() < 3)
                            continue;
                        // první trojúhelník
                        rasterizeTriangle(
                                output.get(0),
                                output.get(1),
                                output.get(2),
                                solid.getShader());

                        // quad -> druhý trojúhelník
                        if (output.size() == 4) {

                            rasterizeTriangle(
                                    output.get(0),
                                    output.get(2),
                                    output.get(3),
                                    solid.getShader());
                        }
                    }
                    break;
                case TopologyType.POINTS:
                    for (int i = 0; i < part.getCount(); i++) {
                        int vertexIndex = solid.getIndexBuffer().get(index + i);
                        Vertex vertex = solid.getVertexBuffer().get(vertexIndex);

                        vertex = new Vertex(vertex.getPosition().mul(finalMatrix), vertex.getColor());

                        // Crop in clip space
                        if (Clipper.clipReject(vertex))
                            continue;

                        Optional<Vec3D> dehomogA = vertex.getPosition().dehomog();

                        if (dehomogA.isEmpty())
                            continue;

                        Vec3D vec3D = transformToWindow(dehomogA.get());

                        pointRasterizer.rasterize(new Vertex(vec3D.getX(), vec3D.getY(), vec3D.getZ()),
                                solid.getShader());
                    }
                    break;
            }
        }

    }

    private Vec3D transformToWindow(Vec3D v) {
        return v.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((width - 1) / 2., (height - 1) / 2., 1));
    }

    private Vec3D computeNormalWithMtrix(Vertex v, Mat4 normalMatrix) {
        Point3D normalPoint = new Point3D(
                v.getNormal().getX(),
                v.getNormal().getY(),
                v.getNormal().getZ(),
                0);

        Point3D transformed = normalPoint.mul(normalMatrix);

        return new Vec3D(
                transformed.getX(),
                transformed.getY(),
                transformed.getZ())
                .normalized()
                .orElse(new Vec3D(0, 0, 1));
    }

    private void rasterizeTriangle(
            Vertex a,
            Vertex b,
            Vertex c,
            Shader shader) {

        Optional<Vec3D> dehomogA = a.getPosition().dehomog();
        Optional<Vec3D> dehomogB = b.getPosition().dehomog();
        Optional<Vec3D> dehomogC = c.getPosition().dehomog();

        if (dehomogA.isEmpty()
                || dehomogB.isEmpty()
                || dehomogC.isEmpty()) {
            return;
        }

        Vec3D screenA = transformToWindow(dehomogA.get());
        Vec3D screenB = transformToWindow(dehomogB.get());
        Vec3D screenC = transformToWindow(dehomogC.get());

        double clipWA = a.getClipW();
        double clipWB = b.getClipW();
        double clipWC = c.getClipW();

        double clipZA = a.getClipZ();
        double clipZB = b.getClipZ();
        double clipZC = c.getClipZ();

        Point3D worldA = a.getWorldPosition();
        Point3D worldB = b.getWorldPosition();
        Point3D worldC = c.getWorldPosition();

        Vertex newA = new Vertex(
                new Point3D(screenA.getX(), screenA.getY(), screenA.getZ()),
                a.getColor(),
                a.getUV(),
                a.getNormal());

        newA.setWorldPosition(worldA);
        newA.setClipW(clipWA);
        newA.setClipZ(clipZA);

        Vertex newB = new Vertex(
                new Point3D(screenB.getX(), screenB.getY(), screenB.getZ()),
                b.getColor(),
                b.getUV(),
                b.getNormal());

        newB.setWorldPosition(worldB);
        newB.setClipW(clipWB);
        newB.setClipZ(clipZB);

        Vertex newC = new Vertex(
                new Point3D(screenC.getX(), screenC.getY(), screenC.getZ()),
                c.getColor(),
                c.getUV(),
                c.getNormal());

        newC.setWorldPosition(worldC);
        newC.setClipW(clipWC);
        newC.setClipZ(clipZC);

        RasterVertex rvA = RasterVertexBuilder.from(newA);
        RasterVertex rvB = RasterVertexBuilder.from(newB);
        RasterVertex rvC = RasterVertexBuilder.from(newC);

        triangleRasterizer.rasterize(rvA, rvB, rvC, shader);
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
