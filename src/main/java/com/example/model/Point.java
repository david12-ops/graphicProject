package com.example.model;

import com.example.transforms.Col;

public class Point {

    private int x, y;
    private Col color;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(Col color) {
        this.color = color;
    }

    public void setColor(int rgb) {
        this.color = new Col(rgb);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Point: " + "x " + x + ", y " + y + "\n" + "color: " + color + "\n";
    }
}
