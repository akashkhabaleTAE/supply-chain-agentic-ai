package com.akash.supplychain.optimization.dto;

import java.util.List;

public record SupplierOptimizationResponse(
        String materialType,
        List<SupplierScoreDto> rankedSuppliers
) {
}
