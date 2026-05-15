package com.example.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.example.transforms.Col;

/**
 * Represents a 2D texture used for texture mapping.
 *
 * <p>
 * The texture internally stores image data loaded from a resource file
 * and provides color sampling using UV coordinates.
 * </p>
 *
 * <p>
 * UV coordinates are expected in the normalized range:
 * </p>
 *
 * :contentReference[oaicite:0]{index=0}
 *
 * <p>
 * Texture sampling converts normalized UV coordinates into pixel coordinates
 * inside the texture image.
 * </p>
 *
 * <p>
 * Current sampling method:
 * </p>
 *
 * <ul>
 * <li>Nearest-neighbor sampling</li>
 * <li>Edge clamping</li>
 * </ul>
 */
public class Texture {
    private final BufferedImage image;

    /**
     * Loads a texture from a classpath resource.
     *
     * @param path resource path to the image file
     * @throws IOException if the image cannot be loaded
     */
    public Texture(String path) throws IOException {
        image = ImageIO.read(
                Objects.requireNonNull(
                        getClass().getResourceAsStream(path)));
    }

    /**
     * Samples a color from the texture using normalized UV coordinates.
     *
     * <p>
     * UV coordinates are converted into texture-space coordinates:
     * </p>
     *
     * :contentReference[oaicite:1]{index=1}
     *
     * <p>
     * Coordinates outside the texture range are clamped to the texture edges.
     * </p>
     *
     * <p>
     * Sampling uses nearest-neighbor filtering.
     * </p>
     *
     * @param u horizontal texture coordinate in range {@code [0, 1]}
     * @param v vertical texture coordinate in range {@code [0, 1]}
     * @return sampled texture color
     */
    public Col sample(double u, double v) {
        int x = (int) (u * (image.getWidth() - 1));
        int y = (int) (v * (image.getHeight() - 1));

        x = Math.max(0, Math.min(image.getWidth() - 1, x));
        y = Math.max(0, Math.min(image.getHeight() - 1, y));

        int rgb = image.getRGB(x, y);

        return new Col(rgb);
    }
}
