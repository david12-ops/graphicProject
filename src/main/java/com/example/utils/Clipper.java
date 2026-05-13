package com.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.model.Vertex;
import com.example.transforms.Point3D;

public class Clipper {
    public static Optional<Vertex[]> clipByZ(Vertex a, Vertex b) {
        float zMin = 0;
        double z1 = a.getPosition().getZ();
        double z2 = b.getPosition().getZ();

        boolean inside1 = z1 >= zMin;
        boolean inside2 = z2 >= zMin;

        if (!inside1 && !inside2) {
            return Optional.empty();
        }

        if (inside1 && inside2) {
            return Optional.of(new Vertex[] { a, b });
        }

        Vertex intersection;

        if (inside1) {
            intersection = intersect(a, b, zMin);
            b = intersection;
        } else {
            intersection = intersect(b, a, zMin);
            a = intersection;
        }

        return Optional.of(new Vertex[] { a, b });
    }

    public static List<Vertex> clipByZ(List<Vertex> input) {
        List<Vertex> output = new ArrayList<>();
        float zMin = 0;

        for (int i = 0; i < input.size(); i++) {
            Vertex current = input.get(i);
            Vertex next = input.get((i + 1) % input.size());

            boolean currentInside = current.getZ() >= zMin;
            boolean nextInside = next.getZ() >= zMin;

            if (currentInside && nextInside) {
                output.add(next);
            } else if (currentInside && !nextInside) {
                output.add(intersect(current, next, zMin));
            } else if (!currentInside && nextInside) {
                output.add(intersect(current, next, zMin));
                output.add(next);
            }
        }

        return output;
    }

    public static Vertex intersect(Vertex inside, Vertex outside, float zMin) {
        double t = (zMin - inside.getZ()) / (outside.getZ() - inside.getZ());

        return inside.mul(1 - t).add(outside.mul(t));
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
}
