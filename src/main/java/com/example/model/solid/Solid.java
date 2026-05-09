package com.example.model.solid;

import com.example.enums.SolidModel;
import com.example.enums.SolidState;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4Identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Solid {
    protected List<Vertex> vertexBuffer = new ArrayList<>();
    protected List<Integer> indexBuffer = new ArrayList<>();
    protected List<Part> partBuffer = new ArrayList<>();
    protected SolidModel solidModel = SolidModel.WIREFRAME;

    private Mat4 model = new Mat4Identity();
    private SolidState state = SolidState.NORMAL;

    private Col solidColor = new Col(0xFFFFFFFF);
    private Col[] gradientColorForLines;
    private Col[] colorsForTriangles;
    // private Col texture;

    private boolean useModelMatrix = true;

    public List<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public List<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public List<Part> getPartBuffer() {
        return partBuffer;
    }

    public Col getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(Col solidColor) {
        this.solidColor = solidColor;
    }

    public Col[] getColorsForGradient() {
        return gradientColorForLines;
    }

    public void setGradientColor(Col start, Col end) {
        this.gradientColorForLines = new Col[] {
                start,
                end
        };
    }

    public Col[] getColorsForTriangles() {
        return colorsForTriangles;
    }

    public void setGradientColor(Col first, Col second, Col third) {
        this.colorsForTriangles = new Col[] {
                first,
                second,
                third
        };
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

    public void addIndices(Integer... indices) {
        indexBuffer.addAll(Arrays.asList(indices));
    }
}
