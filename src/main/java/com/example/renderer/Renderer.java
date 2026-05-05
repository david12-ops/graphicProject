package com.example.renderer;

import com.example.enums.SolidModel;
import com.example.model.solid.Solid;
import com.example.transforms.Mat4;

public class Renderer {
    private final WireTypeRenderer wireTypeRenderer;
    private final SolidTypeRenderer solidTypeRenderer;
    private SolidModel solidModel;

    public Renderer(WireTypeRenderer wireTypeRenderer, SolidTypeRenderer solidTypeRenderer, SolidModel solidModel) {
        this.wireTypeRenderer = wireTypeRenderer;
        this.solidTypeRenderer = solidTypeRenderer;
        this.solidModel = solidModel;
    }

    public void render(Solid solid) {
        if (solidModel == SolidModel.SOLID)
            solidTypeRenderer.render(solid);
        else
            wireTypeRenderer.render(solid);
    }

    public void setSolidModel(SolidModel solidModel) {
        this.solidModel = solidModel;
    }

    public void setView(Mat4 view) {
        if (solidModel == SolidModel.SOLID)
            solidTypeRenderer.setView(view);
        else
            wireTypeRenderer.setView(view);
        ;
    }

    public void setProj(Mat4 proj) {
        if (solidModel == SolidModel.SOLID)
            solidTypeRenderer.setProj(proj);
        else
            wireTypeRenderer.setProj(proj);
    }
}
