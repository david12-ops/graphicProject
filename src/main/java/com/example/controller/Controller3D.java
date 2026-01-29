package com.example.controller;

import com.example.raster.Raster;
import com.example.view.Panel;

public class Controller3D implements Controller {

    /**
     * Creates a new 2D controller for the given panel.
     * 
     * @param panel Panel used for rendering and input handling
     */
    public Controller3D(Panel panel) {
        // this.panel = panel;
        // initObjects(panel.getRaster());
        // initListeners(panel);
    }

    /**
     * Initializes rasterizers and drawable objects.
     *
     * @param raster Raster used for drawing operations
     */
    public void initObjects(Raster raster) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initObjects'");
    }

    @Override
    public void initListeners(Panel panel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initListeners'");
    }
}
