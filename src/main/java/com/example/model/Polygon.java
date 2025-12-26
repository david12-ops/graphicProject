package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private List<Point> points;

    public Polygon() {
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public Point getPoint(int index) {
        return this.points.get(index);
    }

    public int getSize() {
        return this.points.size();
    }
}
