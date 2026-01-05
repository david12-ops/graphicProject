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

    public void removePoint(Point point) {
        if (!this.points.isEmpty() && this.points.contains(point))
            this.points.remove(point);
    }

    public Point getPoint(int index) {
        if (index >= 0 && index < this.points.size())
            return this.points.get(index);
        else
            return null;
    }

    public Point getNearesPoint(int x, int y) {
        double minDist = Double.MAX_VALUE;
        Point draggedVertex = null;

        if (this.points.isEmpty())
            return null;

        for (Point p : points) {
            double dx = x - p.getX();
            double dy = y - p.getY();
            double dist = dx * dx + dy * dy;

            if (dist < minDist) {
                minDist = dist;
                draggedVertex = p;
            }
        }

        return draggedVertex;
    }

    public int getSize() {
        return this.points.size();
    }

    public void clearAllPoints() {
        this.points.clear();
    }
}
