package com.akash.supplychain.agent.dto;

import java.util.List;

public record RiskAssessment(
        DisruptionEvent disruptionEvent,
        List<SupplierImpact> impactedSuppliers,
        List<SupplierDto> supplierNetwork,
        List<InventoryDto> inventoryPositions,
        String mappingSummary
) {
}
