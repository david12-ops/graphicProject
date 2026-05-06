package com.example.raster;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.example.transforms.Col;

/**
 * Raster implementation backed by a {@link BufferedImage}.
 * 
 * Provides pixel-level access and basic drawing operations.
 */
public class RasterBufferedImage implements Raster<Col> {

    private BufferedImage image;
    private Col clearColor;

    /**
     * Returns the underlying buffered image.
     *
     * @return Backing {@link BufferedImage}
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Creates a new raster with the specified dimensions.
     *
     * @param width  Width of the raster in pixels
     * @param height Height of the raster in pixels
     */
    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Draws the raster onto the provided graphics context.
     *
     * @param graphics Graphics context used for rendering
     */
    public void repaint(Graphics graphics) {
        graphics.drawImage(image, 0, 0, null);
    }

    /**
     * Clears the raster using the current clear color
     * and draws another raster on top of it.
     *
     * @param raster Raster to be drawn onto this raster
     */
    public void draw(RasterBufferedImage raster) {
        Graphics graphics = getGraphics();
        graphics.setColor(new Color(clearColor.getRGB()));
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.drawImage(raster.image, 0, 0, null);
    }

    /**
     * Returns a graphics context for drawing into the raster.
     *
     * @return {@link Graphics} object for this raster
     */
    public Graphics getGraphics() {
        return image.getGraphics();
    }

    /**
     * Checks whether the given pixel coordinates lie inside the raster bounds.
     *
     * <p>
     * A coordinate is considered inside if:
     * <ul>
     * <li>{@code x} is in the range {@code [0, image.getWidth())}</li>
     * <li>{@code y} is in the range {@code [0, image.getHeight())}</li>
     * </ul>
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @return {@code true} if the coordinates are inside the raster,
     *         {@code false} otherwise
     */
    private boolean isInsideRaster(int x, int y) {
        return x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight();
    }

    /**
     * Sets the color of a pixel at the given coordinates.
     * Pixels outside the raster are ignored.
     *
     * @param x     X-coordinate of the pixel
     * @param y     Y-coordinate of the pixel
     * @param color Color value in RGB format
     */
    @Override
    public void setValue(int x, int y, Col color) {
        if (isInsideRaster(x, y))
            image.setRGB(x, y, color.getRGB());
    }

    /**
     * Returns the color of the pixel at the given coordinates.
     *
     * @param x X-coordinate of the pixel
     * @param y Y-coordinate of the pixel
     * @return Pixel color value in RGB format or {@code Optional.empty()} if
     *         coordinates are outside the raster
     */
    @Override
    public Optional<Col> getValue(int x, int y) {
        if (!isInsideRaster(x, y))
            return Optional.empty();

        return Optional.of(new Col(image.getRGB(x, y)));
    }

    /**
     * Returns the width of the raster.
     *
     * @return Raster width in pixels
     */
    @Override
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * Returns the height of the raster.
     *
     * @return Raster height in pixels
     */
    @Override
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Clears the raster by removing all drawn content.
     */
    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
    }

    public void setClearColor(int color) {
        this.clearColor = new Col(color);
    }
}
