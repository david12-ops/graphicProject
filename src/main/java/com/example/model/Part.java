package com.example.model;

import com.example.enums.TopologyType;

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
