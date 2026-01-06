package com.example.model;

public class Line {

    private Point a, b;
    private float k, q;

    public Line(int x1, int y1, int x2, int y2) {
        this.a = new Point(x1, x2);
        this.b = new Point(x2, y2);
    }

    public Line(Point p1, Point p2) {
        this.a = p1;
        this.b = p2;
    }

    public void normalize() {
        if (a.getY() > b.getY()) {
            Point tmp = a;
            a = b;
            b = tmp;
        }
    }

    public void compute() {
        if (a.getY() != b.getY()) {
            this.k = (b.getX() - a.getX()) / (b.getY() - a.getY());
            this.q = a.getX() - k * a.getY();
        }
    }

    public void shorten() {
        b = new Point(b.getX(), b.getY() - 1);
    }

    public boolean isIntersection(int y) {
        return y >= a.getY() && y < b.getY();
    }

    public int intersection(int y) {
        return (int) Math.floor(k * y + q);
    }

    public Point getPointA() {
        return this.a;
    }

    public Point getPointB() {
        return this.b;
    }
}
