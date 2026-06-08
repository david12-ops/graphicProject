package com.example.renderer;

import java.util.List;
import java.util.Optional;

import com.example.enums.SolidState;
import com.example.model.Light;
import com.example.model.Part;
import com.example.model.RasterVertex;
import com.example.model.Vertex;
import com.example.model.solid.Solid;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.PointRasterizer;
import com.example.rasterize.TriangleRasterizer;
import com.example.shader.Shader;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4Scale;
import com.example.transforms.Mat4Transl;
import com.example.transforms.Point3D;
import com.example.transforms.Vec3D;
import com.example.utils.Clipper;
import com.example.utils.RasterVertexBuilder;

/**
 * Main rendering pipeline responsible for transforming, clipping,
 * dehomogenizing and rasterizing 3D primitives.
 *
 * <p>
 * The renderer processes scene geometry in several stages:
 * </p>
 *
 * <ol>
 * <li>Model/View/Projection transformation</li>
 * <li>Clip-space clipping</li>
 * <li>Perspective divide (dehomogenization)</li>
 * <li>Viewport transformation</li>
 * <li>Rasterization</li>
 * <li>Fragment shading</li>
 * </ol>
 *
 * <p>
 * Supported primitive topologies:
 * </p>
 *
 * <ul>
 * <li>Points</li>
 * <li>Lines</li>
 * <li>Triangles</li>
 * </ul>
 *
 * <p>
 * Triangles use perspective-correct interpolation through
 * {@link RasterVertex}.
 * </p>
 */
public class Renderer {
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;
    private PointRasterizer pointRasterizer;

    private int width, height;
    private Mat4 view, proj;

    /**
     * Creates a renderer with the specified rasterizers and matrices.
     *
     * @param lineRasterizer     rasterizer used for lines
     * @param triangleRasterizer rasterizer used for triangles
     * @param pointRasterizer    rasterizer used for points
     * @param sceneLight         scene light source
     * @param width              viewport width
     * @param height             viewport height
     * @param view               view matrix
     * @param proj               projection matrix
     */
    public Renderer(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer,
            PointRasterizer pointRasterizer, Light sceneLight, int width,
            int height, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.pointRasterizer = pointRasterizer;
        this.width = width;
        this.height = height;
        this.view = view;
        this.proj = proj;
    }

    /**
     * Renders the specified solid object.
     *
     * <p>
     * Depending on topology type, the renderer performs:
     * </p>
     *
     * <ul>
     * <li>Transformation into clip space</li>
     * <li>Clipping by z</li>
     * <li>Perspective divide</li>
     * <li>Viewport transformation</li>
     * <li>Rasterization</li>
     * </ul>
     *
     * <p>
     * Selected solids are rendered slightly enlarged.
     * </p>
     *
     * @param solid           rendered object
     * @param perspectiveProj rendered with projection or otho
     */
    public void render(Solid solid, boolean perspectiveProj) {
        Vec3D centerVec3d = solid.getCenterVec3d();
        Mat4 finalMatrix = solid.useModelMatrix() ? solid.getModel().mul(view).mul(proj) : (view).mul(proj);

        if (solid.getState() == SolidState.SELECTED) {
            finalMatrix = new Mat4Transl(centerVec3d.opposite())
                    .mul(new Mat4Scale(1.2))
                    .mul(new Mat4Transl(centerVec3d))
                    .mul(finalMatrix);
        }

        for (Part part : solid.getPartBuffer()) {
            int index = part.getStartIndex();
            switch (part.getTopologyType()) {
                case LINES:
                    // barva
                    for (int i = 0; i < part.getCount(); i += 2) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);

                        vecA = new Vertex(vecA.getPosition().mul(finalMatrix), vecA.getColor(), vecA.getUV(),
                                vecA.getNormal());
                        vecB = new Vertex(vecB.getPosition().mul(finalMatrix), vecB.getColor(), vecB.getUV(),
                                vecB.getNormal());

                        // Crop in clip space
                        if (Clipper.clipReject(vecA, vecB))
                            continue;

                        Optional<Vertex[]> clipped = Clipper.clipByZ(vecA, vecB);

                        if (clipped.isEmpty())
                            continue;

                        vecA = clipped.get()[0];
                        vecB = clipped.get()[1];

                        double invW1 = perspectiveProj ? 1.0 / vecA.getPosition().getW() : 1.0;
                        double invW2 = perspectiveProj ? 1.0 / vecB.getPosition().getW() : 1.0;

                        double zOverW1 = perspectiveProj ? vecA.getPosition().getZ() * invW1
                                : vecA.getPosition().getZ();
                        double zOverW2 = perspectiveProj ? vecB.getPosition().getZ() * invW2
                                : vecB.getPosition().getZ();

                        Optional<Vec3D> dehomogA = vecA.getPosition().dehomog();
                        Optional<Vec3D> dehomogB = vecB.getPosition().dehomog();

                        // Dehomogenization
                        if (dehomogA.isEmpty() || dehomogB.isEmpty())
                            continue;

                        // Transform to screen window = NDC -> screen space
                        Vec3D vecA3D = transformToWindow(dehomogA.get());
                        Vec3D vecB3D = transformToWindow(dehomogB.get());

                        lineRasterizer.rasterize(
                                new Vertex(new Point3D(vecA3D.getX(), vecA3D.getY(), vecA3D.getZ()), vecA.getColor(),
                                        vecA.getUV(), vecA.getNormal()),
                                invW1,
                                zOverW1,
                                new Vertex(new Point3D(vecB3D.getX(), vecB3D.getY(), vecB3D.getZ()), vecB.getColor(),
                                        vecB.getUV(), vecB.getNormal()),
                                invW2,
                                zOverW2,
                                solid.getShader());
                    }
                    break;
                case TRIANGLES:
                    Optional<Mat4> invModel = solid.getModel().inverse();

                    Mat4 normalMatrix = invModel
                            .map(Mat4::transpose)
                            .orElse(new Mat4());

                    for (int i = 0; i < part.getCount(); i += 3) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex vecA = solid.getVertexBuffer().get(indexA);
                        Vertex vecB = solid.getVertexBuffer().get(indexB);
                        Vertex vecC = solid.getVertexBuffer().get(indexC);

                        Vec3D worldA = new Vec3D(vecA.getPosition().mul(solid.getModel()));
                        Vec3D worldB = new Vec3D(vecB.getPosition().mul(solid.getModel()));
                        Vec3D worldC = new Vec3D(vecC.getPosition().mul(solid.getModel()));

                        vecA = new Vertex(
                                vecA.getPosition().mul(finalMatrix),
                                vecA.getColor(),
                                vecA.getUV(),
                                computeNormalWithMtrix(vecA, normalMatrix));

                        vecB = new Vertex(
                                vecB.getPosition().mul(finalMatrix),
                                vecB.getColor(),
                                vecB.getUV(),
                                computeNormalWithMtrix(vecB, normalMatrix));

                        vecC = new Vertex(
                                vecC.getPosition().mul(finalMatrix),
                                vecC.getColor(),
                                vecC.getUV(),
                                computeNormalWithMtrix(vecC, normalMatrix));

                        vecA.setWorldPosition(worldA);
                        vecB.setWorldPosition(worldB);
                        vecC.setWorldPosition(worldC);

                        // Crop in clip space
                        if (Clipper.clipReject(vecA, vecB, vecC))
                            continue;

                        // 2. ořezání podle z
                        List<Vertex> output = Clipper.clipByZ(List.of(vecA, vecB, vecC));

                        if (output.size() < 3)
                            continue;

                        // první trojúhelník
                        rasterizeTriangle(
                                output.get(0),
                                output.get(1),
                                output.get(2),
                                perspectiveProj,
                                solid.getShader());

                        // quad -> druhý trojúhelník
                        if (output.size() == 4) {

                            rasterizeTriangle(
                                    output.get(0),
                                    output.get(2),
                                    output.get(3),
                                    perspectiveProj,
                                    solid.getShader());
                        }
                    }
                    break;
                case POINTS:
                    for (int i = 0; i < part.getCount(); i++) {
                        int vertexIndex = solid.getIndexBuffer().get(index + i);
                        Vertex vertex = solid.getVertexBuffer().get(vertexIndex);

                        vertex = new Vertex(vertex.getPosition().mul(finalMatrix), vertex.getColor());

                        // Crop in clip space
                        if (Clipper.clipReject(vertex))
                            continue;

                        Optional<Vec3D> dehomogA = vertex.getPosition().dehomog();

                        if (dehomogA.isEmpty())
                            continue;

                        Vec3D vec3D = transformToWindow(dehomogA.get());

                        pointRasterizer.rasterize(new Vertex(vec3D.getX(), vec3D.getY(), vec3D.getZ()),
                                solid.getShader());
                    }
                    break;
            }
        }

    }

    /**
     * Converts normalized device coordinates into screen coordinates.
     *
     * <p>
     * Performs viewport transformation:
     * </p>
     *
     * :contentReference[oaicite:0]{index=0}
     *
     * <p>
     * and flips the Y axis.
     * </p>
     *
     * @param v normalized device coordinates
     * @return screen-space coordinates
     */
    private Vec3D transformToWindow(Vec3D v) {
        return v.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((width - 1) / 2., (height - 1) / 2., 1));
    }

    /**
     * Transforms a vertex normal using the inverse-transpose matrix.
     *
     * <p>
     * Normals must be transformed differently than positions:
     * </p>
     *
     * :contentReference[oaicite:1]{index=1}
     *
     * <p>
     * This preserves correct orientation under non-uniform scaling.
     * </p>
     *
     * @param v            source vertex
     * @param normalMatrix inverse-transpose normal matrix
     * @return transformed normalized normal
     */
    private Vec3D computeNormalWithMtrix(Vertex v, Mat4 normalMatrix) {
        Point3D normalPoint = new Point3D(
                v.getNormal().getX(),
                v.getNormal().getY(),
                v.getNormal().getZ(),
                0);

        Point3D transformed = normalPoint.mul(normalMatrix);

        return new Vec3D(
                transformed.getX(),
                transformed.getY(),
                transformed.getZ())
                .normalized()
                .orElse(new Vec3D(0, 0, 1));
    }

    /**
     * Rasterizes a single triangle.
     *
     * <p>
     * The method:
     * </p>
     *
     * <ol>
     * <li>Performs perspective divide</li>
     * <li>Transforms vertices into screen space</li>
     * <li>Builds perspective-correct raster vertices</li>
     * <li>Delegates rasterization to triangle rasterizer</li>
     * </ol>
     *
     * @param a               first vertex
     * @param b               second vertex
     * @param c               third vertex
     * @param perspectiveProj rendered with projection or otho
     * @param shader          fragment shader
     */
    private void rasterizeTriangle(
            Vertex a,
            Vertex b,
            Vertex c,
            boolean perspectiveProj,
            Shader shader) {

        double w1 = a.getPosition().getW();
        double w2 = b.getPosition().getW();
        double w3 = c.getPosition().getW();

        Optional<Vec3D> dehomogA = a.getPosition().dehomog();
        Optional<Vec3D> dehomogB = b.getPosition().dehomog();
        Optional<Vec3D> dehomogC = c.getPosition().dehomog();

        if (dehomogA.isEmpty()
                || dehomogB.isEmpty()
                || dehomogC.isEmpty()) {
            return;
        }

        Vec3D screenA = transformToWindow(dehomogA.get());
        Vec3D screenB = transformToWindow(dehomogB.get());
        Vec3D screenC = transformToWindow(dehomogC.get());

        Vec3D worldA = a.getWorldPosition();
        Vec3D worldB = b.getWorldPosition();
        Vec3D worldC = c.getWorldPosition();

        Vertex newA = new Vertex(
                new Point3D(screenA.getX(), screenA.getY(), screenA.getZ(), w1),
                a.getColor(),
                a.getUV(),
                a.getNormal());

        newA.setWorldPosition(worldA);

        Vertex newB = new Vertex(
                new Point3D(screenB.getX(), screenB.getY(), screenB.getZ(), w2),
                b.getColor(),
                b.getUV(),
                b.getNormal());

        newB.setWorldPosition(worldB);

        Vertex newC = new Vertex(
                new Point3D(screenC.getX(), screenC.getY(), screenC.getZ(), w3),
                c.getColor(),
                c.getUV(),
                c.getNormal());

        newC.setWorldPosition(worldC);

        RasterVertex rvA = RasterVertexBuilder.from(newA, perspectiveProj);
        RasterVertex rvB = RasterVertexBuilder.from(newB, perspectiveProj);
        RasterVertex rvC = RasterVertexBuilder.from(newC, perspectiveProj);

        triangleRasterizer.rasterize(rvA, rvB, rvC, shader);
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
