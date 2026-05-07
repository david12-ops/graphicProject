package com.example.model.solid;

import com.example.enums.TopologyType;
import com.example.model.Part;
import com.example.model.Vertex;
import com.example.transforms.Col;

public class Arrow extends Solid {
    public Arrow() {
        vertexBuffer.add(new Vertex(0, 0, 0)); // v0
        vertexBuffer.add(new Vertex(0.8, 0, 0)); // v1
        vertexBuffer.add(new Vertex(0.8, 0, -0.2, new Col(0xff0000))); // v2
        vertexBuffer.add(new Vertex(1, 0, 0)); // v3
        vertexBuffer.add(new Vertex(0.8, 0, 0.2)); // v4

        addIndices(0, 1); // lines
        addIndices(4, 3, 2); // triangles

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
        partBuffer.add(new Part(TopologyType.TRIANGLES, 2, 1));
    }
}
