package com.example.model;

public class Line {

    private Point a, b;
    private double k, q;

    public Line(int x1, int y1, int x2, int y2) {
        this.a = new Point(x1, y1);
        this.b = new Point(x2, y2);
    }

    public Line(Point p1, Point p2) {
        this.a = p1;
        this.b = p2;
    }

    /**
     * Ensures that the edge is oriented from top to bottom.
     * 
     * After normalization, point {@code a} always has
     * a smaller or equal y-coordinate than point {@code b}.
     */
    public void normalize() {
        if (a.getY() > b.getY()) {
            Point tmp = a;
            a = b;
            b = tmp;
        }
    }

    /**
     * Computes line parameters used for scanline intersection.
     * 
     * Calculates coefficients {@code k} and {@code q} for the
     * line equation: {@code x = k * y + q}.
     */
    public void compute() {
        if (a.getY() != b.getY()) {
            this.k = (b.getX() - a.getX()) / (double) (b.getY() - a.getY());
            this.q = a.getX() - k * a.getY();
        }
    }

    /**
     * Determines whether a horizontal scanline
     * intersects this edge at the given y-coordinate.
     *
     * @param y Y-coordinate of the scanline
     * @return {@code true} if the scanline intersects the edge
     */
    public boolean isIntersection(int y) {
        return y >= a.getY() && y < b.getY();
    }

    /**
     * Computes the x-coordinate of the intersection
     * between this edge and a horizontal scanline.
     *
     * @param y Y-coordinate of the scanline
     * @return X-coordinate of the intersection point
     */
    public double intersection(int y) {
        return k * y + q;
    }

    public Point getPointA() {
        return this.a;
    }

    public Point getPointB() {
        return this.b;
    }

    @Override
    public String toString() {
        return "Start point: " + "x " + a.getX() + ", y " + a.getY() + "\n" + "End point: " + "x " + b.getX() + ", y "
                + b.getY() + "\n" + "k: " + k + ", q" + q + "\n";
    }
}
