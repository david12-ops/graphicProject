package com.example.model.solid;

import com.example.enums.SolidState;
import com.example.transforms.Col;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4Identity;
import com.example.transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public abstract class Solid {
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>();
    protected Col color = new Col(0xffffff);
    protected Mat4 model = new Mat4Identity();
    protected SolidState state = SolidState.NORMAL;

    public List<Point3D> getVb() {
        return vb;
    }

    public List<Integer> getIb() {
        return ib;
    }

    public Col getColor() {
        return color;
    }

    public Mat4 getModel() {
        return model;
    }

    public SolidState getState() {
        return state;
    }

    public void setState(SolidState state) {
        this.state = state;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }
}
