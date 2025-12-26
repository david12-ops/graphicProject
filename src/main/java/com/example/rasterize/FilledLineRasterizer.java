package com.example.rasterize;

import java.awt.Graphics;

import com.example.raster.Raster;
import com.example.raster.RasterBufferedImage;

public class FilledLineRasterizer extends LineRasterizer {

    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    // TODO - s vyplnenim
    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        Graphics g = ((RasterBufferedImage) raster).getImg().getGraphics();
        g.setColor(this.color);
        g.drawLine(x1, y1, x2, y2);
    }

}
