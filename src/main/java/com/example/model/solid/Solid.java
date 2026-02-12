package com.example.model.solid;

import com.example.enums.SolidState;
import com.example.model.Polygon;
import com.example.transforms.Col;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4Identity;
import com.example.transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public abstract class Solid {
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>();
    protected Mat4 model = new Mat4Identity();
    protected List<Polygon> solidPolygons = new ArrayList<>();

    private SolidState state = SolidState.NORMAL;

    private Col solidColor = new Col(0xffffff);
    private Col gradientStart;
    private Col gradientEnd;

    private boolean useModelMatrix = true;

    public List<Point3D> getVb() {
        return vb;
    }

    public List<Integer> getIb() {
        return ib;
    }

    public Col getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(Col solidColor) {
        this.solidColor = solidColor;
    }

    public Col[] getColorsForGradient() {
        return new Col[] { gradientStart, gradientEnd };
    }

    public void setGradientColor(Col start, Col end) {
        this.gradientStart = start;
        this.gradientEnd = end;
    }

    public List<Polygon> getSolidPolygons() {
        return solidPolygons;
    }

    public void setSolidPolygons(List<Polygon> solidPolygons) {
        this.solidPolygons = solidPolygons;
    }

    public SolidState getState() {
        return state;
    }

    public void setState(SolidState state) {
        this.state = state;
    }

    public boolean useModelMatrix() {
        return useModelMatrix;
    }

    public void setUseModelMatrix(boolean useModelMatrix) {
        this.useModelMatrix = useModelMatrix;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }
}
