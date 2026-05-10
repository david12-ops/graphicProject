package com.example.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.example.transforms.Col;

public class Texture {
    private final BufferedImage image;

    public Texture(String path) throws IOException {
        image = ImageIO.read(
                Objects.requireNonNull(
                        getClass().getResourceAsStream(path)));
    }

    public Col sample(double u, double v) {
        int x = (int) (u * (image.getWidth() - 1));
        int y = (int) (v * (image.getHeight() - 1));

        x = Math.max(0, Math.min(image.getWidth() - 1, x));
        y = Math.max(0, Math.min(image.getHeight() - 1, y));

        int rgb = image.getRGB(x, y);

        return new Col(rgb);
    }
}
