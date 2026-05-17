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
                Vec3D lightVec = subtract(
                                sceneLight.getPosition(),
                                v.getWorldPosition());

                Optional<Vec3D> lOpt = lightVec
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

                double distance = lightVec.length();

                Vec3D L = lOpt.get();
                Vec3D V = vOpt.get();

                /*
                 * Light attenuation (distance falloff)
                 *
                 * Simulates how light intensity decreases with distance.
                 *
                 * Formula:
                 *
                 * attenuation = 1.0 /
                 * (kc + kl * d + kq * d * d)
                 *
                 * where:
                 *
                 * d = distance from light source to fragment
                 *
                 * kc = constant attenuation
                 * Base light intensity.
                 * Usually 1.0 to avoid division by zero.
                 *
                 * kl = linear attenuation
                 * Controls linear light falloff over distance.
                 *
                 * kq = quadratic attenuation
                 * Controls quadratic falloff (physically inspired).
                 * Higher values make the light fade faster.
                 *
                 * Example values:
                 *
                 * kc = 1.0
                 * kl = 0.04
                 * kq = 0.002
                 *
                 * Result:
                 * - close fragments receive strong lighting
                 * - distant fragments become darker
                 * - improves depth perception and realism
                 */
                double attenuation = 1.0 /
                                (1.0 + 0.04 * distance + 0.002 * distance * distance);

                // === AMBIENT ===
                double ambientStrength = 0.24;

                // === DIFFUSE ===
                double diff = Math.max(0.0, N.dot(L));
                double diffuseStrength = 0.16;

                // === SPECULAR ===
                double specularStrength = 0.38;
                double shininess = 10;

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
                double diffuse = diffuseStrength * diff * attenuation;
                double specular = specularStrength * spec * attenuation;

                Col base = v.getColor();

                Col light = sceneLight.getColor();

                return new Col(
                                base.getR() * ambient +
                                                base.getR() * light.getR() * diffuse +
                                                light.getR() * specular,

                                base.getG() * ambient +
                                                base.getG() * light.getG() * diffuse +
                                                light.getG() * specular,

                                base.getB() * ambient +
                                                base.getB() * light.getB() * diffuse +
                                                light.getB() * specular);
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
