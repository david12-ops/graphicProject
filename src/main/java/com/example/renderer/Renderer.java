package com.example.renderer;

import com.example.rasterize.LineRasterizer;
import com.example.model.solid.Solid;
import com.example.transforms.Mat4;
import com.example.transforms.Point3D;
import com.example.transforms.Vec3D;
import com.example.model.Point;

public class Renderer {
    private LineRasterizer lineRasterizer;
    private int width, heigth;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, int width, int heigth, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.width = width;
        this.heigth = heigth;
        this.view = view;
        this.proj = proj;
    }

    public void renderSolid(Solid solid) {
        for (int i = 0; i < solid.getIb().size() - 1; i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D pointA = solid.getVb().get(indexA);
            Point3D pointB = solid.getVb().get(indexB);

            // Modelovací transformace (model) = model space -> world space
            pointA = pointA.mul(solid.getModel());
            pointB = pointB.mul(solid.getModel());

            // pointA = pointA.mul(solid.getModel()).mul(view).mul(proj);

            // Pohledová tranformace (view) = world space -> view space
            pointA = pointA.mul(view);
            pointB = pointB.mul(view);

            // Projekční tranformace (projection) = view space -> clip space
            pointA = pointA.mul(proj);
            pointB = pointB.mul(proj);

            // TODO: Ořezání - slide 88

            // TODO: Dehomogenizace - x, y, z, w = x/w, y/w, z/w, w/w = NDC
            // pouzor raději ošetřit dělení nulou
            pointA = pointA.mul(1 / pointA.getW());
            pointB = pointB.mul(1 / pointB.getW());

            // Transformace do okna obrazovky = NDC -> screen space
            Vec3D vecA = transformToWindow(pointA);
            Vec3D vecB = transformToWindow(pointB);

            lineRasterizer.rasterize(new Point((int) Math.round(vecA.getX()), (int) Math.round(vecA.getY())),
                    new Point((int) Math.round(vecB.getX()), (int) Math.round(vecB.getY())));

        }
    }

    private Vec3D transformToWindow(Point3D p) {
        return new Vec3D(p).mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((width - 1) / 2., (heigth - 1) / 2., 1));
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
