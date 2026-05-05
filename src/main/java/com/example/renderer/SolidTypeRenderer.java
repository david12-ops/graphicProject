package com.example.renderer;

import java.util.Optional;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.model.solid.Solid;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.TriangleRasterizer;
import com.example.shader.Shader;
import com.example.shader.ShaderConstant;
import com.example.transforms.Mat4;
import com.example.transforms.Point3D;
import com.example.transforms.Vec3D;

public class SolidTypeRenderer {
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;
    private Shader shader;
    private int width, height;
    private Mat4 view, proj;

    public SolidTypeRenderer(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer, int width,
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
                    int index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);

                        Point3D pointA = solid.getVertexBuffer().get(indexA).getPosition();
                        Point3D pointB = solid.getVertexBuffer().get(indexB).getPosition();

                        // pronásobit MVP maticí
                        pointA = pointA.mul(solid.getModel()).mul(view).mul(proj);
                        pointB = pointB.mul(solid.getModel()).mul(view).mul(proj);

                        // Crop in clip space
                        if (!insideClipVolume(pointA) && !insideClipVolume(pointB))
                            continue;

                        Optional<Vec3D> dehomogA = pointA.dehomog();
                        Optional<Vec3D> dehomogB = pointB.dehomog();

                        // Dehomogenization
                        if (dehomogA.isEmpty() || dehomogB.isEmpty())
                            continue;

                        // Transform to screen window = NDC -> screen space
                        Vec3D vecA = transformToWindow(dehomogA.get());
                        Vec3D vecB = transformToWindow(dehomogB.get());

                        // lineRasterizer.rasterize(new Point((int) Math.round(vecA.getX()), (int)
                        // Math.round(vecA.getY())),
                        // new Point((int) Math.round(vecB.getX()), (int) Math.round(vecB.getY())));

                        lineRasterizer.rasterize(vecA, vecB);
                    }
                    break;
                case TopologyType.TRIANGLES:
                    index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);
                        Vertex vecC = solid.getVertexBuffer().get(indexC);

                        // pronásobit MVP maticí
                        Point3D pointA = vecA.getPosition().mul(solid.getModel()).mul(view).mul(proj);
                        Point3D pointB = vecB.getPosition().mul(solid.getModel()).mul(view).mul(proj);
                        Point3D pointC = vecC.getPosition().mul(solid.getModel()).mul(view).mul(proj);

                        // Crop in clip space
                        if (!insideClipVolume(pointA) && !insideClipVolume(pointB))
                            continue;

                        // 2. ořezání podle z
                        float zMin = 0;
                        if (vecA.getZ() < zMin)
                            continue;

                        if (vecB.getZ() < zMin) {
                            // spočítám nový trojúhelník a budu ho rasterizovat
                        }

                        if (vecC.getZ() < zMin) {
                            // vzniknou dva nové trojúhelníky
                        }

                        Optional<Vec3D> dehomogA = pointA.dehomog();
                        Optional<Vec3D> dehomogB = pointB.dehomog();
                        Optional<Vec3D> dehomogC = pointC.dehomog();

                        // Dehomogenization
                        if (dehomogA.isEmpty() || dehomogB.isEmpty() || dehomogC.isEmpty())
                            continue;

                        // Transform to screen window = NDC -> screen space
                        Vec3D vecA3D = transformToWindow(dehomogA.get());
                        Vec3D vecB3D = transformToWindow(dehomogB.get());
                        Vec3D vecC3D = transformToWindow(dehomogC.get());

                        triangleRasterizer.rasterize(
                                new Vertex(new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ())),
                                new Vertex(new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ())),
                                new Vertex(new Point3D(vecC3D.getX(), vecC3D.getY(), vecC3D.getZ())), shader);
                    }
                    break;
                case TopologyType.POINTS:
                    System.out.println("Points topology is not supported for solid type renderer.");
                    break;
            }
        }
    }

    private boolean insideClipVolume(Point3D p) {
        double w = p.getW();

        return p.getX() >= -w && p.getX() <= w &&
                p.getY() >= -w && p.getY() <= w && p.getZ() >= 0
                && p.getZ() <= w;
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
