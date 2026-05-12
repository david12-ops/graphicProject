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
import com.example.shader.PhongShader;
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

    private final Light sceneLight;
    private Shader phongShader;

    private int width, height;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer,
            PointRasterizer pointRasterizer, Light sceneLight, int width,
            int height, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.pointRasterizer = pointRasterizer;
        this.sceneLight = sceneLight;
        this.width = width;
        this.height = height;
        this.view = view;
        this.proj = proj;
    }

    public void render(Solid solid) {
        Vec3D centerVec3d = solid.getCenterVec3d();
        Mat4 finalMatrix = solid.useModelMatrix() ? solid.getModel().mul(view).mul(proj) : (view).mul(proj);

        if (solid.getUsePongShader()) {
            phongShader = new PhongShader(sceneLight);
            solid.setShader(phongShader);
        }

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
                    // Mat4 normalMatrix = solid.getModel().inverse().transpose(); - pro pohyb
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
                                vecA.getNormal());

                        vecB = new Vertex(
                                vecB.getPosition().mul(finalMatrix),
                                vecB.getColor(),
                                vecB.getUV(),
                                vecB.getNormal());

                        vecC = new Vertex(
                                vecC.getPosition().mul(finalMatrix),
                                vecC.getColor(),
                                vecC.getUV(),
                                vecC.getNormal());

                        vecA.setWorldPosition(worldA);
                        vecB.setWorldPosition(worldB);
                        vecC.setWorldPosition(worldC);

                        // Crop in clip space
                        if (Clipper.clipReject(vecA, vecB, vecC))
                            continue;

                        // 2. ořezání podle z TODO
                        List<Vertex> output = Clipper.clipByZ(List.of(vecA, vecB, vecC));

                        vecA = output.get(0);
                        vecB = output.get(1);
                        vecC = output.get(2);

                        if (output.size() == 3) {
                            Optional<Vec3D> dehomogA = output.get(0).getPosition().dehomog();
                            Optional<Vec3D> dehomogB = output.get(1).getPosition().dehomog();
                            Optional<Vec3D> dehomogC = output.get(2).getPosition().dehomog();

                            // Dehomogenization
                            if (dehomogA.isEmpty() || dehomogB.isEmpty() || dehomogC.isEmpty())
                                continue;

                            // Transform to screen window = NDC -> screen space
                            Vec3D vecA3D = transformToWindow(dehomogA.get());
                            Vec3D vecB3D = transformToWindow(dehomogB.get());
                            Vec3D vecC3D = transformToWindow(dehomogC.get());

                            Point3D interpolatedWorldA = vecA.getWorldPosition();
                            Point3D interpolatedWorldB = vecB.getWorldPosition();
                            Point3D interpolatedWorldC = vecC.getWorldPosition();

                            vecA = new Vertex(
                                    new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ()),
                                    vecA.getColor(),
                                    vecA.getUV(),
                                    vecA.getNormal());

                            vecA.setWorldPosition(interpolatedWorldA);

                            vecB = new Vertex(
                                    new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ()),
                                    vecB.getColor(),
                                    vecB.getUV(),
                                    vecB.getNormal());

                            vecB.setWorldPosition(interpolatedWorldB);

                            vecC = new Vertex(
                                    new Point3D(vecC3D.getX(), vecC3D.getY(), vecC3D.getZ()),
                                    vecC.getColor(),
                                    vecC.getUV(),
                                    vecC.getNormal());

                            vecC.setWorldPosition(interpolatedWorldC);

                            RasterVertex rvA = RasterVertexBuilder.from(vecA);
                            RasterVertex rvB = RasterVertexBuilder.from(vecB);
                            RasterVertex rvC = RasterVertexBuilder.from(vecC);

                            triangleRasterizer.rasterize(rvA, rvB, rvC);
                        } else if (output.size() == 4) {
                            Optional<Vec3D> dehomogA = output.get(0).getPosition().dehomog();
                            Optional<Vec3D> dehomogB = output.get(1).getPosition().dehomog();
                            Optional<Vec3D> dehomogC = output.get(2).getPosition().dehomog();

                            // Dehomogenization
                            if (dehomogA.isEmpty() || dehomogB.isEmpty() || dehomogC.isEmpty())
                                continue;

                            // Transform to screen window = NDC -> screen space
                            Vec3D vecA3D = transformToWindow(dehomogA.get());
                            Vec3D vecB3D = transformToWindow(dehomogB.get());
                            Vec3D vecC3D = transformToWindow(dehomogC.get());

                            Point3D interpolatedWorldA = vecA.getWorldPosition();
                            Point3D interpolatedWorldB = vecB.getWorldPosition();
                            Point3D interpolatedWorldC = vecC.getWorldPosition();

                            vecA = new Vertex(
                                    new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ()),
                                    vecA.getColor(),
                                    vecA.getUV(),
                                    vecA.getNormal());

                            vecA.setWorldPosition(interpolatedWorldA);

                            vecB = new Vertex(
                                    new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ()),
                                    vecB.getColor(),
                                    vecB.getUV(),
                                    vecB.getNormal());

                            vecB.setWorldPosition(interpolatedWorldB);

                            vecC = new Vertex(
                                    new Point3D(vecC3D.getX(), vecC3D.getY(), vecC3D.getZ()),
                                    vecC.getColor(),
                                    vecC.getUV(),
                                    vecC.getNormal());

                            vecC.setWorldPosition(interpolatedWorldC);

                            RasterVertex rvA = RasterVertexBuilder.from(vecA);
                            RasterVertex rvB = RasterVertexBuilder.from(vecB);
                            RasterVertex rvC = RasterVertexBuilder.from(vecC);

                            triangleRasterizer.rasterize(rvA, rvB, rvC);

                            // Druhý
                            Vertex secA = output.get(0);
                            Vertex secB = output.get(2);
                            Vertex secC = output.get(3);

                            Optional<Vec3D> dehomogASec = secA.getPosition().dehomog();
                            Optional<Vec3D> dehomogBSec = secB.getPosition().dehomog();
                            Optional<Vec3D> dehomogCSec = secC.getPosition().dehomog();

                            // Dehomogenization
                            if (dehomogASec.isEmpty() || dehomogBSec.isEmpty() || dehomogCSec.isEmpty())
                                continue;

                            // Transform to screen window = NDC -> screen space
                            vecA3D = transformToWindow(dehomogASec.get());
                            vecB3D = transformToWindow(dehomogBSec.get());
                            vecC3D = transformToWindow(dehomogCSec.get());

                            Point3D interpolatedWorldASec = secA.getWorldPosition();
                            Point3D interpolatedWorldBSec = secB.getWorldPosition();
                            Point3D interpolatedWorldCSec = secC.getWorldPosition();

                            secA = new Vertex(
                                    new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ()),
                                    secA.getColor(),
                                    secA.getUV(),
                                    secA.getNormal());

                            secA.setWorldPosition(interpolatedWorldASec);

                            secB = new Vertex(
                                    new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ()),
                                    secB.getColor(),
                                    secB.getUV(),
                                    secB.getNormal());

                            secB.setWorldPosition(interpolatedWorldBSec);

                            secC = new Vertex(
                                    new Point3D(vecC3D.getX(), vecC3D.getY(), vecC3D.getZ()),
                                    secC.getColor(),
                                    secC.getUV(),
                                    secC.getNormal());

                            secC.setWorldPosition(interpolatedWorldCSec);

                            rvA = RasterVertexBuilder.from(secA);
                            rvB = RasterVertexBuilder.from(secB);
                            rvC = RasterVertexBuilder.from(secC);

                            triangleRasterizer.rasterize(rvA, rvB, rvC);
                        } else
                            continue;
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

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
