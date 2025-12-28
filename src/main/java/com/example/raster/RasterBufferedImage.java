package com.example.raster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster {

    private BufferedImage image;
    private int color;

    public BufferedImage getImg() {
        return image;
    }

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void repaint(Graphics graphics) {
        graphics.drawImage(image, 0, 0, null);
    }

    public void draw(RasterBufferedImage raster) {
        Graphics graphics = getGraphics();
        graphics.setColor(new Color(color));
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.drawImage(raster.image, 0, 0, null);
    }

    public Graphics getGraphics() {
        return image.getGraphics();
    }

    @Override
    public void setPixel(int x, int y, int color) {
        // TODO: ošetřit zápis mimo raster
        image.setRGB(x, y, color);
    }

    @Override
    public int getPixel(int x, int y) {
        // TODO: ošetřit načtení mimo raster

        // chci vrátit hodnotu
        return image.getRGB(x, y);
        // jsem mimo raster
        // return OptionalInt.empty();

    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void setClearColor(int color) {
        this.color = color;
    }

}
