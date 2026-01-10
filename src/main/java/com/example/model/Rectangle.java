package com.example.model;

/**
 * Represents a rectangle defined by one base edge and a height point.
 */
public class Rectangle extends Polygon {

    /**
     * Constructs a rectangle from a base edge and a height-defining point.
     * 
     * The rectangle is defined by the base edge {@code AB} and a third point
     * {@code heightPoint}, which determines both the height and the orientation
     * of the rectangle via projection onto the normal of {@code AB}.
     *
     * 
     * @param a         First point of the base edge
     * @param b         Second point of the base edge
     * @param heigPoint Point defining the height and orientation of the rectangle
     */
    public Rectangle(Point a, Point b, Point heigPoint) {
        createRectangle(a, b, heigPoint);
    }

    /**
     * Computes rectangle vertices and adds them to the polygon.
     * 
     * The method:
     * 
     * computes the direction vector of the base edge {@code AB},
     * derives a normalized perpendicular (normal) vector,
     * computes the rectangle height using scalar projection,
     * constructs the remaining two vertices by offsetting points
     * {@code A} and {@code B} along the normal</li>
     *
     * The resulting vertices are added in correct order so that the polygon
     * can be properly rendered and filled.
     *
     * 
     * @param a         First point of the base edge
     * @param b         Second point of the base edge
     * @param heigPoint Point defining the height and orientation of the rectangle
     */
    private void createRectangle(Point a, Point b, Point heigPoint) {
        clearAllPoints();

        // Direction vector AB
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();

        // Length of AB
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) {
            return;
        }

        // Normalized perpendicular vector to AB
        double nx = -dy / length;
        double ny = dx / length;

        // Height of the rectangle (projection onto the normal)
        double height = (heigPoint.getX() - a.getX()) * nx + (heigPoint.getY() - a.getY()) * ny;

        if (height < 0) {
            height = -height;
            nx = -nx;
            ny = -ny;
        }

        Point p1 = a;
        Point p2 = b;
        Point p3 = new Point((int) Math.round(b.getX() + nx * height), (int) Math.round(b.getY() + ny * height));
        Point p4 = new Point((int) Math.round(a.getX() + nx * height), (int) Math.round(a.getY() + ny * height));

        addPoint(p1);
        addPoint(p2);
        addPoint(p3);
        addPoint(p4);
    }
}
