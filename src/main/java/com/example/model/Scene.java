package com.example.model;

import java.util.ArrayList;
import java.util.List;

import com.example.model.solid.Solid;

public class Scene {

    private final List<Solid> solids = new ArrayList<>();

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

    public void clear() {
        solids.clear();
    }
}
