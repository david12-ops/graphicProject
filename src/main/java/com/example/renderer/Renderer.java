package com.example.renderer;

import java.util.Optional;

import com.example.enums.ColorMode;
import com.example.enums.SolidState;
import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.model.solid.Solid;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.TriangleRasterizer;
import com.example.shader.Shader;
import com.example.shader.ShaderConstant;
import com.example.transforms.Col;
import com.example.transforms.Mat4;
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
        initColorForRasterizer(solid);

        if (solid.getState() == SolidState.SELECTED)
            lineRasterizer.setSelectedColor(new Col(255, 255, 0)); // Yellow if selected
        else
            lineRasterizer.setSelectedColor((Col) null);

        for (Part part : solid.getPartBuffer()) {
            switch (part.getTopologyType()) {
                case TopologyType.LINES:
                    int index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i += 2) {
                        int indexA = solid.getIndexBuffer().get(i);
                        int indexB = solid.getIndexBuffer().get(i + 1);

                        Point3D pointA = solid.getVertexBuffer().get(indexA).getPosition();
                        Point3D pointB = solid.getVertexBuffer().get(indexB).getPosition();

                        if (solid.useModelMatrix()) {
                            // Modeling transformation (model) = model space -> world space
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            pointA = pointA.mul(solid.getModel()).mul(view).mul(proj);
                            pointB = pointB.mul(solid.getModel()).mul(view).mul(proj);
                        } else {
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            pointA = pointA.mul(view).mul(proj);
                            pointB = pointB.mul(view).mul(proj);
                        }

                        // Crop in clip space
                        if (!insideClipVolume(pointA) && !insideClipVolume(pointB))
                            continue;

                        double invW1 = 1.0 / pointA.getW();
                        double invW2 = 1.0 / pointB.getW();

                        double zOverW1 = pointA.getZ() * invW1;
                        double zOverW2 = pointB.getZ() * invW2;

                        Optional<Vec3D> dehomogA = pointA.dehomog();
                        Optional<Vec3D> dehomogB = pointB.dehomog();

                        // Dehomogenization
                        if (dehomogA.isEmpty() || dehomogB.isEmpty())
                            continue;

                        // Transform to screen window = NDC -> screen space
                        Vec3D vecA = transformToWindow(dehomogA.get());
                        Vec3D vecB = transformToWindow(dehomogB.get());

                        // lineRasterizer.rasterize(vecA, vecB);
                        lineRasterizer.rasterize(vecA.getX(), vecA.getY(), invW1, zOverW1, vecB.getX(), vecB.getY(),
                                invW2,
                                zOverW2);
                    }
                    break;
                case TopologyType.TRIANGLES:
                    index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i += 3) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);
                        Vertex vecC = solid.getVertexBuffer().get(indexC);

                        if (solid.useModelMatrix()) {
                            // Modeling transformation (model) = model space -> world space
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            vecA = new Vertex(vecA.getPosition().mul(solid.getModel()).mul(view).mul(proj));
                            vecB = new Vertex(vecB.getPosition().mul(solid.getModel()).mul(view).mul(proj));
                            vecC = new Vertex(vecC.getPosition().mul(solid.getModel()).mul(view).mul(proj));

                        } else {
                            // View transformation (view) = world space -> view space
                            // Projection transformation (projection) = view space -> clip space
                            vecA = new Vertex(vecA.getPosition().mul(view).mul(proj));
                            vecB = new Vertex(vecB.getPosition().mul(view).mul(proj));
                            vecC = new Vertex(vecC.getPosition().mul(view).mul(proj));
                        }

                        // Crop in clip space
                        if (!insideClipVolume(vecA.getPosition()) && !insideClipVolume(vecB.getPosition())
                                && !insideClipVolume(vecC.getPosition()))
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
                                new Vertex(new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ())),
                                new Vertex(new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ())),
                                new Vertex(new Point3D(vecC3D.getX(), vecC3D.getY(), vecC3D.getZ())), shader);
                    }
                    break;
                case TopologyType.POINTS:
                    for (int i = 0; i < part.getCount(); i++) {
                        index = solid.getIndexBuffer().get(part.getStartIndex() + i);
                        Point3D point3d = solid.getVertexBuffer().get(index).getPosition();

                        point3d = point3d.mul(solid.getModel())
                                .mul(view)
                                .mul(proj);

                        if (!insideClipVolume(point3d))
                            continue;

                        Optional<Vec3D> dehomogA = point3d.dehomog();

                        if (dehomogA.isEmpty())
                            continue;

                        Vec3D vec3D = transformToWindow(dehomogA.get());

                        lineRasterizer.rasterize(vec3D);
                    }
                    break;
            }
        }
    }

    private void initColorForRasterizer(Solid solid) {
        if (lineRasterizer.getColorMode() == ColorMode.SOLID)
            lineRasterizer.setSolidColor(solid.getSolidColor());
        else if (lineRasterizer.getColorMode() == ColorMode.GRADIENT)
            lineRasterizer.setGradientColors(solid.getColorsForGradient()[0],
                    solid.getColorsForGradient()[1]);
        else {
            System.err.println("Invalid ColorMode, falling back to SOLID");
            lineRasterizer.setSolidColor(solid.getSolidColor());
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
