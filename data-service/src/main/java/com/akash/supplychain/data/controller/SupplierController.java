package com.akash.supplychain.data.controller;

import com.akash.supplychain.data.dto.SupplierDto;
import com.akash.supplychain.data.service.DataQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
public class SupplierController {

    private final DataQueryService dataQueryService;

    public SupplierController(DataQueryService dataQueryService) {
        this.dataQueryService = dataQueryService;
    }

    @GetMapping("/suppliers")
    public List<SupplierDto> findAllSuppliers() {
        return dataQueryService.findAllSuppliers();
    }

    @GetMapping("/suppliers/{supplierId}")
    public SupplierDto findSupplier(@PathVariable("supplierId") Long supplierId) {
        return dataQueryService.findSupplier(supplierId);
    }

    @GetMapping("/supply-chains/{supplyChainId}/suppliers")
    public List<SupplierDto> findSuppliersBySupplyChain(@PathVariable("supplyChainId") Long supplyChainId) {
        return dataQueryService.findSuppliersBySupplyChain(supplyChainId);
    }

    @GetMapping("/supply-chains/{supplyChainId}/suppliers/high-risk")
    public List<SupplierDto> findHighRiskSuppliers(@PathVariable("supplyChainId") Long supplyChainId) {
        return dataQueryService.findHighRiskSuppliers(supplyChainId);
    }
}
