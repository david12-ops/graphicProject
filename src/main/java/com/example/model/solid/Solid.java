package com.example.model.solid;

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

    public void setModel(Mat4 model) {
        this.model = model;
    }
}
