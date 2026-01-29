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

    public void renderSolid(Solid solid, boolean isSelected) {
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
            if (outsideEdge(pointA) && outsideEdge(pointB))
                continue;

            if (pointA.getW() <= 0 || pointB.getW() <= 0)
                continue;

            // TODO: Dehomogenizace - x, y, z, w = x/w, y/w, z/w, w/w = NDC
            pointA = pointA.mul(1.0 / pointA.getW());
            pointB = pointB.mul(1.0 / pointB.getW());

            // Transformace do okna obrazovky = NDC -> screen space
            Vec3D vecA = transformToWindow(pointA);
            Vec3D vecB = transformToWindow(pointB);

            // int c1 = isSelected ? 0xFFFF0000 : v1.getColorARGB(); // Red if selected
            // int c2 = isSelected ? 0xFFFF0000 : v2.getColorARGB();

            lineRasterizer.rasterize(new Point((int) Math.round(vecA.getX()), (int) Math.round(vecA.getY())),
                    new Point((int) Math.round(vecB.getX()), (int) Math.round(vecB.getY())));
        }
    }

    private boolean outsideEdge(Point3D p) {
        // všechna x jsou větší než -w a
        // všechna x jsou menší než w a
        // všechna y jsou větší než -w a
        // šechna y jsou menší než w a
        // šechna z jsou větší než 0 a
        // všechna z jsou menší než w => splněné všechny podmínky => bod je mimo hranici

        return p.getX() > -p.getW() && p.getX() < p.getW() &&
                p.getY() > -p.getW() && p.getY() < p.getW() && p.getZ() > 0
                && p.getZ() < p.getW();
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
