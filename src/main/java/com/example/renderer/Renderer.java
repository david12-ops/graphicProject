package com.example.renderer;

import com.example.rasterize.LineRasterizer;
import com.example.model.solid.Solid;
import com.example.model.Point;
import com.example.transforms.Col;
import com.example.transforms.Mat4;
import com.example.transforms.Point3D;
import com.example.transforms.Vec3D;

import java.util.Optional;

import com.example.enums.ColorMode;
import com.example.enums.SolidState;

public class Renderer {

    private LineRasterizer lineRasterizer;
    private int width, height;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, int width, int height, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.width = width;
        this.height = height;
        this.view = view;
        this.proj = proj;
    }

    public void renderSolid(Solid solid) {

        initColorForRasterizer(solid);

        if (solid.getState() == SolidState.SELECTED)
            lineRasterizer.setSelectedColor(new Col(255, 255, 0)); // Yellow if selected
        else
            lineRasterizer.setSelectedColor((Col) null);

        for (int i = 0; i < solid.getIb().size() - 1; i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D pointA = solid.getVb().get(indexA);
            Point3D pointB = solid.getVb().get(indexB);

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

            Optional<Vec3D> dehomogA = pointA.dehomog();
            Optional<Vec3D> dehomogB = pointB.dehomog();

            // Dehomogenization
            if (dehomogA.isEmpty() || dehomogB.isEmpty())
                continue;

            // Transform to screen window = NDC -> screen space
            Vec3D vecA = transformToWindow(dehomogA.get());
            Vec3D vecB = transformToWindow(dehomogB.get());

            lineRasterizer.rasterize(new Point((int) Math.round(vecA.getX()), (int) Math.round(vecA.getY())),
                    new Point((int) Math.round(vecB.getX()), (int) Math.round(vecB.getY())));
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

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
