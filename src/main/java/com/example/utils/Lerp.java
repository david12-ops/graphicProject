package com.example.utils;

import com.example.model.Vectorazible;

public class Lerp<E extends Vectorazible<E>> {

    public E lerp(E v1, E v2, double t) {
        return v1.mul(1 - t).add(v2.mul(t));
    }
}
