package com.example.rasterize;

import com.example.model.Line;
import com.example.raster.Raster;

/* 
Nevýhoda: je to funkce - „jednomu x odpovídá právě jedno y“ 
jak řešit svislou úsečku?
násobení a sčítání v plovoucí řádové čárce
neefektivní!
Výhoda: postup použitelný i pro složitější křivky
*/

public class FilledLineRasterizer extends LineRasterizer {

    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
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

            for (int x = x1; x < x2; x++) {
                int y = Math.round(k * x + q);
                raster.setPixel(x, y, 0xff0000);
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

            for (int y = y1; y < y2; y++) {
                int x = Math.round((y - q) / k);
                raster.setPixel(x, y, 0xff0000);
            }
        }
    }

    @Override
    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }
}
