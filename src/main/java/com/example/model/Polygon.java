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

    public void clearAllPoints() {
        this.points.clear();
    }

    public void removePoint(Point point) {
        if (this.points.contains(point))
            this.points.remove(point);
    }

    public Point getPoint(int index) {
        return this.points.get(index);
    }

    public int getSize() {
        return this.points.size();
    }
}
