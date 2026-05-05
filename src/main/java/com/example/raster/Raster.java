package com.example.raster;

import java.util.Optional;

public interface Raster<E> {

    Optional<E> getValue(int x, int y);

    void setValue(int x, int y, E value);

    int getWidth();

    int getHeight();

    void clear();
}
