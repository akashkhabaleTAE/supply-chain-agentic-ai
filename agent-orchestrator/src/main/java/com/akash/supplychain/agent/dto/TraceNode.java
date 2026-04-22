package com.akash.supplychain.agent.dto;

import java.util.List;

public record TraceNode(
        String nodeId,
        String nodeType,
        String label,
        Integer tier,
        double propagatedRisk,
        List<String> connectedNodes
) {
}
