package com.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.model.Vertex;
import com.example.transforms.Point3D;

/**
 * Utility class responsible for clipping primitives against the view frustum.
 *
 * <p>
 * The clipping is performed in homogeneous clip space before perspective
 * divide.
 * The valid DirectX-style clip volume is:
 * </p>
 *
 * <pre>
 * -w <= x <= w
 * -w <= y <= w
 *  0 <= z <= w
 * </pre>
 *
 * <p>
 * This class provides:
 * </p>
 * <ul>
 * <li>Line clipping against the near plane</li>
 * <li>Polygon clipping against the near plane</li>
 * <li>Fast clip rejection tests for points, lines, and triangles</li>
 * </ul>
 */
public class Clipper {
    public static Lerp<Vertex> lerp = new Lerp<>();

    public static Optional<Vertex[]> clipByZ(Vertex a, Vertex b) {
        // ===== NEAR PLANE =====

        boolean insideA = planeDistanceNear(a) >= 0;

        boolean insideB = planeDistanceNear(b) >= 0;

        if (!insideA && !insideB) {
            return Optional.empty();
        }

        if (insideA && !insideB) {

            b = intersectNear(a, b);

        } else if (!insideA && insideB) {

            a = intersectNear(a, b);
        }

        // ===== FAR PLANE =====

        insideA = planeDistanceFar(a) >= 0;

        insideB = planeDistanceFar(b) >= 0;

        if (!insideA && !insideB) {
            return Optional.empty();
        }

        if (insideA && !insideB) {

            b = intersectFar(a, b);

        } else if (!insideA && insideB) {

            a = intersectFar(a, b);
        }

        return Optional.of(new Vertex[] { a, b });
    }

    public static List<Vertex> clipByZ(List<Vertex> input) {
        input = clipNear(input);

        if (input.isEmpty()) {
            return input;
        }

        input = clipFar(input);

        return input;
    }

    public static boolean clipReject(Vertex v1, Vertex v2) {
        Point3D p1 = v1.getPosition();
        Point3D p2 = v2.getPosition();

        return (p1.getX() < -p1.getW() && p2.getX() < -p2.getW()) ||
                (p1.getX() > p1.getW() && p2.getX() > p2.getW()) ||

                (p1.getY() < -p1.getW() && p2.getY() < -p2.getW()) ||
                (p1.getY() > p1.getW() && p2.getY() > p2.getW()) ||

                (p1.getZ() < 0 && p2.getZ() < 0) ||
                (p1.getZ() > p1.getW() && p2.getZ() > p2.getW());
    }

    public static boolean clipReject(Vertex v1, Vertex v2, Vertex v3) {
        Point3D p1 = v1.getPosition();
        Point3D p2 = v2.getPosition();
        Point3D p3 = v3.getPosition();

        return (p1.getX() < -p1.getW() && p2.getX() < -p2.getW() && p3.getX() < -p3.getW()) ||
                (p1.getX() > p1.getW() && p2.getX() > p2.getW() && p3.getX() > p3.getW()) ||

                (p1.getY() < -p1.getW() && p2.getY() < -p2.getW() && p3.getY() < -p3.getW()) ||
                (p1.getY() > p1.getW() && p2.getY() > p2.getW() && p3.getY() > p3.getW()) ||

                (p1.getZ() < 0 && p2.getZ() < 0 && p3.getZ() < 0) ||
                (p1.getZ() > p1.getW() && p2.getZ() > p2.getW() && p3.getZ() > p3.getW());
    }

    public static boolean clipReject(Vertex v) {
        Point3D p = v.getPosition();
        double w = p.getW();

        return p.getX() < -w || p.getX() > w ||
                p.getY() < -w || p.getY() > w ||
                p.getZ() < 0 || p.getZ() > w;
    }

    private static Vertex intersectNear(Vertex a, Vertex b) {

        double da = planeDistanceNear(a);
        double db = planeDistanceNear(b);

        double t = da / (da - db);

        return lerp.lerp(a, b, t);
    }

    private static Vertex intersectFar(Vertex a, Vertex b) {

        double da = planeDistanceFar(a);
        double db = planeDistanceFar(b);

        double t = da / (da - db);

        return lerp.lerp(a, b, t);
    }

    private static List<Vertex> clipFar(List<Vertex> input) {

        List<Vertex> output = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {

            Vertex current = input.get(i);
            Vertex next = input.get((i + 1) % input.size());

            boolean currentInside = planeDistanceFar(current) >= 0;

            boolean nextInside = planeDistanceFar(next) >= 0;

            if (currentInside && nextInside) {

                output.add(next);

            } else if (currentInside && !nextInside) {

                output.add(intersectFar(current, next));

            } else if (!currentInside && nextInside) {

                output.add(intersectFar(current, next));
                output.add(next);
            }
        }

        return output;
    }

    private static List<Vertex> clipNear(List<Vertex> input) {

        List<Vertex> output = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {

            Vertex current = input.get(i);
            Vertex next = input.get((i + 1) % input.size());

            boolean currentInside = planeDistanceNear(current) >= 0;

            boolean nextInside = planeDistanceNear(next) >= 0;

            if (currentInside && nextInside) {

                output.add(next);

            } else if (currentInside && !nextInside) {

                output.add(intersectNear(current, next));

            } else if (!currentInside && nextInside) {

                output.add(intersectNear(current, next));
                output.add(next);
            }
        }

        return output;
    }

    private static double planeDistanceNear(Vertex v) {
        return v.getZ();
    }

    private static double planeDistanceFar(Vertex v) {
        return v.getPosition().getW() - v.getZ();
    }
}
