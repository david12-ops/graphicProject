package com.example.view;

import com.example.enums.ColorDrawMode;
import com.example.enums.ColorFillMode;
import com.example.raster.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Panel extends JPanel {

    private RasterBufferedImage raster;
    private ColorFillMode colorFillMode = ColorFillMode.CONSTANT;
    private ColorDrawMode colorDrawingMode = ColorDrawMode.SOLID;
    private final int clearColor = Color.BLACK.getRGB();

    public RasterBufferedImage getRaster() {
        return raster;
    }

    private static final int FPS = 1000 / 20;
    public static final int WIDTH = 800, HEIGHT = 600;

    Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        raster = new RasterBufferedImage(WIDTH, HEIGHT);
        raster.setClearColor(clearColor);
        setLoop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
        // pro zájemce - co dělá observer - https://stackoverflow.com/a/1684476
    }

    public ColorFillMode getColorFillMode() {
        return this.colorFillMode;
    }

    public void setColorFillMode(ColorFillMode colorFillMode) {
        this.colorFillMode = colorFillMode;
    }

    public ColorDrawMode getColorDrawMode() {
        return colorDrawingMode;
    }

    public void setColorDrawMode(ColorDrawMode colorDrawingMode) {
        this.colorDrawingMode = colorDrawingMode;
    }

    public void resize() {
        if (this.getWidth() < 1 || this.getHeight() < 1)
            return;

        // no resize if new is // smaller
        if (this.getWidth() <= raster.getWidth() && this.getHeight() <= raster.getHeight())
            return;

        RasterBufferedImage newRaster = new RasterBufferedImage(this.getWidth(), this.getHeight());
        newRaster.setClearColor(this.clearColor);
        newRaster.draw(raster);
        raster = newRaster;
    }

    private void setLoop() {
        // časovač, který 30 krát za vteřinu obnoví obsah plátna aktuálním img
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, FPS);
    }

    public void clear() {
        raster.clear();
    }
}
