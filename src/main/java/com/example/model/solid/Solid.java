package com.example.model.solid;

import com.example.enums.SolidModel;
import com.example.enums.SolidState;
import com.example.model.Part;
import com.example.model.Texture;
import com.example.model.Vertex;
import com.example.shader.Shader;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4Identity;
import com.example.transforms.Vec3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Solid {
    protected List<Vertex> vertexBuffer = new ArrayList<>();
    protected List<Integer> indexBuffer = new ArrayList<>();
    protected List<Part> partBuffer = new ArrayList<>();

    protected SolidModel solidModel = SolidModel.WIREFRAME;
    private SolidState state = SolidState.NORMAL;

    private Vec3D centerPoint3d;
    private Mat4 model = new Mat4Identity();
    private Texture texture;
    private boolean useModelMatrix = true;
    private Shader shader;
    private boolean usePongShader = false;

    public List<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public List<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public List<Part> getPartBuffer() {
        return partBuffer;
    }

    public void setUsePongShader(boolean usePongShader) {
        this.usePongShader = usePongShader;
    }

    public boolean getUsePongShader() {
        return usePongShader;
    }

    public SolidState getState() {
        return state;
    }

    public void setState(SolidState state) {
        this.state = state;
    }

    public boolean useModelMatrix() {
        return useModelMatrix;
    }

    public void setUseModelMatrix(boolean useModelMatrix) {
        this.useModelMatrix = useModelMatrix;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public void setSolidTopology(SolidModel solidModel) {
        this.solidModel = solidModel;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

    public void computeCenter() {
        double x = 0;
        double y = 0;
        double z = 0;

        for (Vertex v : vertexBuffer) {
            x += v.getX();
            y += v.getY();
            z += v.getZ();
        }

        centerPoint3d = new Vec3D(x / vertexBuffer.size(), y / vertexBuffer.size(), z / vertexBuffer.size());
    }

    public Vec3D getCenterVec3d() {
        return centerPoint3d;
    }

    public void addIndices(Integer... indices) {
        indexBuffer.addAll(Arrays.asList(indices));
    }
}
