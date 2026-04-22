package com.akash.supplychain.agent.dto;

import java.util.List;

public record MitigationPlanRequest(
        Long supplyChainId,
        String disruptionTitle,
        String materialType,
        String origin,
        String destination,
        String severity,
        Double exposureScore,
        List<SupplierCandidateDto> candidateSuppliers,
        List<RouteOptionDto> routeOptions,
        List<InventoryPositionDto> inventoryPositions
) {
}
