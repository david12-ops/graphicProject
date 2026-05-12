package com.example.model;

import java.util.ArrayList;
import java.util.List;

import com.example.model.solid.Solid;

public class Scene {

    private final List<Solid> solids = new ArrayList<>();
    private Light sceneLight;

    public Scene() {
    }

    public void addSolid(Solid solid) {
        solids.add(solid);
    }

    public void removeSolid(Solid solid) {
        solids.remove(solid);
    }

    public List<Solid> getSolids() {
        return solids;
    }

    public Light getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(Light sceneLight) {
        this.sceneLight = sceneLight;
    }

    public void clear() {
        solids.clear();
    }
}
