package com.example.rasterize;

import java.awt.Color;

import com.example.ColorMode;
import com.example.model.Line;
import com.example.raster.Raster;

/* 
Nevýhoda: je to funkce - „jednomu x odpovídá právě jedno y“ 
jak řešit svislou úsečku?
násobení a sčítání v plovoucí řádové čárce
neefektivní!
Výhoda: postup použitelný i pro složitější křivky
*/

// TODO - ve druhe casti pridat vyhlazeni

public class FilledLineRasterizer extends LineRasterizer {
    private ColorMode colorMode;

    public FilledLineRasterizer(Raster raster, ColorMode colorMode) {
        super(raster);
        this.colorMode = colorMode;
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        trivialAlgorithm(x1, y1, x2, y2);
    }

    @Override
    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    private void trivialAlgorithm(int x1, int y1, int x2, int y2) {
        // y = kx + q
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        if (Math.abs((y2 - y1)) < Math.abs(x2 - x1)) {
            if (x2 < x1) {
                int t;
                t = x1;
                x1 = x2;
                x2 = t;
                t = y1;
                y1 = y2;
                y2 = t;
            }

            if (colorMode == ColorMode.SOLID) {
                for (int x = x1; x < x2; x++) {
                    int y = Math.round(k * x + q);
                    raster.setPixel(x, y, color.getRGB());
                }
            } else if (colorMode == ColorMode.GRADIENT && (endColor != null && startColor != null)) {
                for (int x = x1; x < x2; x++) {
                    int y = Math.round(k * x + q);
                    float w = (x - x1) / (float) (x2 - x1);

                    raster.setPixel(x, y, computeColor(w, startColor, endColor));
                }
            }

        } else {
            if (y2 < y1) {
                int t;
                t = x1;
                x1 = x2;
                x2 = t;
                t = y1;
                y1 = y2;
                y2 = t;
            }

            if (colorMode == ColorMode.SOLID) {
                for (int y = y1; y < y2; y++) {
                    int x = Math.round((y - q) / k);
                    raster.setPixel(x, y, color.getRGB());
                }
            } else if (colorMode == ColorMode.GRADIENT) {
                for (int y = y1; y < y2; y++) {
                    int x = Math.round((y - q) / k);
                    float w = (y - y1) / (float) (y2 - y1);

                    raster.setPixel(x, y, computeColor(w, startColor, endColor));
                }
            }
        }
    }

    private int computeColor(float w, Color startColor, Color endColor) {

        if (w < 0f)
            w = 0f;
        else if (w > 1f)
            w = 1f;

        // For each channel (R, G, B):
        int r = Math.round(startColor.getRed() * (1 - w) + endColor.getRed() * w);
        int g = Math.round(startColor.getGreen() * (1 - w) + endColor.getGreen() * w);
        int b = Math.round(startColor.getBlue() * (1 - w) + endColor.getBlue() * w);

        // revert back
        return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xFF);
    }
}
