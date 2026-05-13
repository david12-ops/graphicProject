package com.example.shader;

import com.example.model.Light;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
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

                // Normála
                Vec3D N = v.getNormal()
                                .normalized()
                                .orElse(new Vec3D(0, 0, 1));

                // Směr ke světlu
                Vec3D L = subtract(
                                sceneLight.getPosition(),
                                v.getWorldPosition())
                                .normalized()
                                .orElse(new Vec3D(0, 0, 1));

                // Směr ke kameře
                Vec3D V = subtract(
                                new Point3D(cameraPosition.getX(), cameraPosition.getY(),
                                                cameraPosition.getZ()),
                                v.getWorldPosition())
                                .normalized()
                                .orElse(new Vec3D(0, 0, 1));

                // HALF VECTOR (Blinn-Phong)
                Vec3D H = L.add(V).normalized().orElse(new Vec3D(0, 0, 1));

                // === AMBIENT ===
                double ambientStrength = 0.2;

                // === DIFFUSE ===
                double diff = Math.max(0.0, N.dot(L));
                double diffuseStrength = 0.8;

                // === SPECULAR ===
                double specularStrength = 0.5;
                double shininess = 32;

                double spec = 0.0;

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

        private Vec3D subtract(Point3D a, Point3D b) {
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
