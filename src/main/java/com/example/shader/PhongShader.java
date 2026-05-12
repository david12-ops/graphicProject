package com.example.shader;

import com.example.model.Light;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Point3D;
import com.example.transforms.Vec3D;

public class PhongShader implements Shader {

    private final Light sceneLight;

    public PhongShader(Light sceneLight) {
        this.sceneLight = sceneLight;
    }

    @Override
    public Col getColor(Vertex v) {
        // Pozice vertexu
        Vec3D position = new Vec3D(v.getWorldPosition());

        // Normála
        Vec3D N = v.getNormal()
                .normalized()
                .orElseThrow();

        // Směr ke světlu
        Vec3D negativeVec = position.mul(-1);
        Point3D point3d = new Point3D(negativeVec.getX(), negativeVec.getY(), negativeVec.getZ());

        Vec3D L = new Vec3D(sceneLight.getPosition()
                .add(point3d))
                .normalized()
                .orElseThrow();

        // Směr ke kameře
        // kamera v (0,0,0)
        Vec3D V = position.opposite()
                .normalized()
                .orElseThrow();

        // HALF VECTOR (Blinn-Phong)
        Vec3D H = L.add(V).normalized().orElseThrow();

        // === AMBIENT ===
        double ambientStrength = 0.2;

        // === DIFFUSE ===
        double diff = Math.max(0.0, N.dot(L));
        double diffuseStrength = 0.8;

        // === SPECULAR ===
        double specularStrength = 0.5;
        double shininess = 32;

        double spec = Math.pow(Math.max(0.0, N.dot(H)), shininess);

        double intensity = ambientStrength +
                diffuseStrength * diff +
                specularStrength * spec;

        intensity = Math.max(0.0,
                Math.min(1.0, intensity));

        Col base = v.getColor();

        return new Col(
                clamp(base.getR() * intensity),
                clamp(base.getG() * intensity),
                clamp(base.getB() * intensity));
    }

    private int clamp(double value) {
        return (int) Math.max(0,
                Math.min(255, value));
    }
}
