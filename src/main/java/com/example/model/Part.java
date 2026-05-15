package com.example.model;

import com.example.enums.TopologyType;

/**
 * Represents a renderable section of indexed geometry.
 *
 * <p>
 * A {@code Part} defines:
 * </p>
 *
 * <ul>
 * <li>The topology type used for rendering</li>
 * <li>The starting index inside the index buffer</li>
 * <li>The number of indices belonging to the part</li>
 * </ul>
 *
 * <p>
 * Parts allow a single mesh to contain multiple primitive types,
 * materials, or logical sections while sharing the same vertex buffer.
 * </p>
 *
 * <p>
 * Typical topology types include:
 * </p>
 *
 * <ul>
 * <li>Points</li>
 * <li>Lines</li>
 * <li>Triangles</li>
 * </ul>
 *
 * <p>
 * Example:
 * </p>
 *
 * <ul>
 * <li>{@code startIndex = 0}</li>
 * <li>{@code count = 36}</li>
 * </ul>
 *
 * <p>
 * meaning the renderer processes 36 indices starting from index 0.
 * </p>
 */
public class Part {
    private final TopologyType topologyType;
    private final int startIndex;
    private final int count;

    public Part(TopologyType topologyType, int startIndex, int count) {
        this.topologyType = topologyType;
        this.startIndex = startIndex;
        this.count = count;
    }

    public TopologyType getTopologyType() {
        return topologyType;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getCount() {
        return count;
    }
}
