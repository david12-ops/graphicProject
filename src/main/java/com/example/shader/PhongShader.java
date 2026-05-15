package com.example.shader;

import java.util.Optional;

import com.example.model.Light;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Vec3D;

/**
 * Fragment shader implementing Blinn-Phong illumination.
 *
 * <p>
 * The shader computes lighting per fragment using:
 * </p>
 *
 * <ul>
 * <li>Ambient lighting</li>
 * <li>Diffuse Lambert shading</li>
 * <li>Specular Blinn-Phong highlights</li>
 * </ul>
 *
 * <p>
 * Lighting calculations are performed in world space using interpolated
 * fragment normals and positions.
 * </p>
 *
 * <p>
 * The final color is computed as:
 * </p>
 *
 * :contentReference[oaicite:0]{index=0}
 *
 * <p>
 * where:
 * </p>
 *
 * <ul>
 * <li>{@code A} = ambient component</li>
 * <li>{@code D} = diffuse component</li>
 * <li>{@code S} = specular component</li>
 * </ul>
 *
 * <p>
 * Specular lighting uses the Blinn-Phong half-vector model:
 * </p>
 *
 * :contentReference[oaicite:1]{index=1}
 *
 * <p>
 * and:
 * </p>
 *
 * :contentReference[oaicite:2]{index=2}
 */
public class PhongShader implements Shader {

        private final Light sceneLight;
        private Vec3D cameraPosition;

        /**
         * Creates a new Phong shader using the specified light source.
         *
         * @param sceneLight scene light used for illumination
         */
        public PhongShader(Light sceneLight) {
                this.sceneLight = sceneLight;
        }

        /**
         * Computes the final shaded color for a fragment.
         *
         * <p>
         * The method evaluates:
         * </p>
         *
         * <ul>
         * <li>Ambient term</li>
         * <li>Diffuse Lambert term</li>
         * <li>Blinn-Phong specular term</li>
         * </ul>
         *
         * <p>
         * If the normal or direction vectors cannot be normalized,
         * the original vertex color is returned.
         * </p>
         *
         * @param v fragment vertex containing interpolated attributes
         * @return shaded fragment color
         */
        public Col getColor(Vertex v) {

                // Camera must be defined for view-dependent lighting
                if (cameraPosition == null) {
                        return v.getColor();
                }

                // Normalize surface normal
                Optional<Vec3D> normalized = v.getNormal().normalized();

                if (normalized.isEmpty()) {
                        System.out.println("ZERO NORMAL!");
                        return v.getColor();
                }

                // Surface normal
                Vec3D N = normalized.get();

                // Direction to light source
                Optional<Vec3D> lOpt = subtract(
                                sceneLight.getPosition(),
                                v.getWorldPosition())
                                .normalized();

                // Direction to camera
                Optional<Vec3D> vOpt = subtract(
                                new Vec3D(cameraPosition.getX(), cameraPosition.getY(),
                                                cameraPosition.getZ()),
                                v.getWorldPosition())
                                .normalized();

                if (lOpt.isEmpty() || vOpt.isEmpty()) {
                        return v.getColor();
                }

                Vec3D L = lOpt.get();
                Vec3D V = vOpt.get();

                // === AMBIENT ===
                double ambientStrength = 0.2;

                // === DIFFUSE ===
                double diff = Math.max(0.0, N.dot(L));
                double diffuseStrength = 0.8;

                // === SPECULAR ===
                double specularStrength = 0.5;
                double shininess = 32;

                double spec = 0.0;

                // HALF VECTOR (Blinn-Phong)
                Optional<Vec3D> hNorm = L.add(V).normalized();

                if (hNorm.isEmpty()) {
                        return v.getColor();
                }

                Vec3D H = hNorm.get();

                // Specular highlight only on front-facing surfaces
                if (diff > 0.0) {
                        spec = Math.pow(
                                        Math.max(0.0, N.dot(H)),
                                        shininess);
                }

                double ambient = ambientStrength;
                double diffuse = diffuseStrength * diff;
                double specular = specularStrength * spec;
                double specularColor = 255.0 * specular;

                Col base = v.getColor();

                return new Col(
                                clamp(base.getR() * (ambient + diffuse) + specularColor),
                                clamp(base.getG() * (ambient + diffuse) + specularColor),
                                clamp(base.getB() * (ambient + diffuse) + specularColor));
        }

        /**
         * Clamps a color channel value to the valid range {@code [0, 255]}.
         *
         * @param value input color value
         * @return clamped color value
         */
        private double clamp(double value) {
                return Math.max(0.0,
                                Math.min(255, value));
        }

        private Vec3D subtract(Vec3D a, Vec3D b) {
                return new Vec3D(
                                a.getX() - b.getX(),
                                a.getY() - b.getY(),
                                a.getZ() - b.getZ());
        }

        @Override
        public void setCameraPosition(Vec3D cameraPosition) {
                this.cameraPosition = cameraPosition;
        }
}
