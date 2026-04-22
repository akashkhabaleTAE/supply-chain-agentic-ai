package com.akash.supplychain.optimization.controller;

import com.akash.supplychain.optimization.dto.InventoryOptimizationRequest;
import com.akash.supplychain.optimization.dto.InventoryOptimizationResponse;
import com.akash.supplychain.optimization.dto.MitigationPlanRequest;
import com.akash.supplychain.optimization.dto.MitigationPlanResponse;
import com.akash.supplychain.optimization.dto.RouteOptimizationRequest;
import com.akash.supplychain.optimization.dto.RouteOptimizationResponse;
import com.akash.supplychain.optimization.dto.SupplierOptimizationRequest;
import com.akash.supplychain.optimization.dto.SupplierOptimizationResponse;
import com.akash.supplychain.optimization.solver.InventoryOptimizerService;
import com.akash.supplychain.optimization.solver.MitigationPlannerService;
import com.akash.supplychain.optimization.solver.RouteOptimizerService;
import com.akash.supplychain.optimization.solver.SupplierOptimizerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/optimization")
public class OptimizationController {

    private final MitigationPlannerService mitigationPlannerService;
    private final SupplierOptimizerService supplierOptimizerService;
    private final RouteOptimizerService routeOptimizerService;
    private final InventoryOptimizerService inventoryOptimizerService;

    public OptimizationController(MitigationPlannerService mitigationPlannerService,
                                  SupplierOptimizerService supplierOptimizerService,
                                  RouteOptimizerService routeOptimizerService,
                                  InventoryOptimizerService inventoryOptimizerService) {
        this.mitigationPlannerService = mitigationPlannerService;
        this.supplierOptimizerService = supplierOptimizerService;
        this.routeOptimizerService = routeOptimizerService;
        this.inventoryOptimizerService = inventoryOptimizerService;
    }

    @PostMapping("/mitigations/plan")
    public MitigationPlanResponse planMitigations(@Valid @RequestBody MitigationPlanRequest request) {
        return mitigationPlannerService.plan(request);
    }

    @PostMapping("/suppliers/rank")
    public SupplierOptimizationResponse rankSuppliers(@Valid @RequestBody SupplierOptimizationRequest request) {
        return new SupplierOptimizationResponse(
                request.materialType(),
                supplierOptimizerService.rankSuppliers(request.materialType(), request.severity(), request.candidateSuppliers())
        );
    }

    @PostMapping("/routes/optimize")
    public RouteOptimizationResponse optimizeRoutes(@Valid @RequestBody RouteOptimizationRequest request) {
        return routeOptimizerService.optimize(request);
    }

    @PostMapping("/inventory/recommendations")
    public InventoryOptimizationResponse recommendInventory(@Valid @RequestBody InventoryOptimizationRequest request) {
        return inventoryOptimizerService.optimize(request);
    }
}
