package com.example.shader;

import java.util.Optional;

import com.example.model.Light;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Vec3D;

public class PhongShader implements Shader {

        private final Light sceneLight;
        private Vec3D cameraPosition;

        public PhongShader(Light sceneLight) {
                this.sceneLight = sceneLight;
        }

        public Col getColor(Vertex v) {

                if (cameraPosition == null) {
                        return v.getColor();
                }

                Optional<Vec3D> normalized = v.getNormal().normalized();

                if (normalized.isEmpty()) {
                        System.out.println("ZERO NORMAL!");
                        return v.getColor();
                }

                // Normála
                Vec3D N = normalized.get();

                // Směr ke světlu a směr ke kameře
                Optional<Vec3D> lOpt = subtract(
                                sceneLight.getPosition(),
                                v.getWorldPosition())
                                .normalized();

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
