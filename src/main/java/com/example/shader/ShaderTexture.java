package com.example.shader;

import com.example.model.Texture;
import com.example.model.Vertex;
import com.example.transforms.Col;
import com.example.transforms.Vec2D;

public class ShaderTexture implements Shader {
    private final Texture texture;

    public ShaderTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Col getColor(Vertex pixel) {
        Vec2D uv = pixel.getUV();

        return texture.sample(uv.getX(), uv.getY());
    }
}
