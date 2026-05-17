package com.example.utils;

import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Vec2D;
import com.example.transforms.Vec3D;

/**
 * Utility class for converting a {@link Vertex} from the graphics pipeline
 * into a {@link RasterVertex} used during rasterization.
 *
 * <p>
 * The builder prepares attributes for perspective-correct interpolation by
 * dividing selected values by the clip-space {@code w} coordinate.
 * </p>
 *
 * <h2>Stored values</h2>
 * <ul>
 * <li><b>Position</b> - screen/NDC position used for rasterization.</li>
 * <li><b>invW</b> - reciprocal value {@code 1 / w} used for
 * perspective correction.</li>
 * <li><b>z</b> - depth value used for depth testing.</li>
 * <li><b>worldOverW</b> - world-space position divided by {@code w}.</li>
 * <li><b>normalOverW</b> - vertex normal prepared for interpolation.</li>
 * <li><b>uvOverW</b> - texture coordinates divided by {@code w}.</li>
 * <li><b>colorOverW</b> - vertex color divided by {@code w}.</li>
 * </ul>
 *
 * <p>
 * During fragment reconstruction, interpolated attributes are restored by
 * dividing them by the interpolated {@code invW}.
 * </p>
 */
public class RasterVertexBuilder {

    /**
     * Converts a pipeline {@link Vertex} into a {@link RasterVertex}.
     *
     * <p>
     * Attributes that require perspective-correct interpolation are multiplied
     * by {@code 1 / w} before rasterization.
     * </p>
     *
     * @param v               source vertex in clip/screen pipeline representation
     * @param perspectiveProj rendered with projection or otho
     * 
     * @return raster-ready vertex with perspective-correct attributes
     */
    public static RasterVertex from(Vertex v, boolean perspectiveProj) {

        double invW = perspectiveProj
                ? 1.0 / v.getPosition().getW()
                : 1.0;

        Vec3D pos = new Vec3D(v.getPosition());

        double z = v.getZ();

        Vec3D worldOverW = v.getWorldPosition().mul(invW);

        Vec3D normalOverW = v.getNormal().mul(invW);

        Vec2D uvOverW = v.getUV().mul(invW);

        Col colorOverW = v.getColor().mul(invW);

        return new RasterVertex(
                pos,
                invW,
                z,
                worldOverW,
                normalOverW,
                uvOverW,
                colorOverW);
    }
}