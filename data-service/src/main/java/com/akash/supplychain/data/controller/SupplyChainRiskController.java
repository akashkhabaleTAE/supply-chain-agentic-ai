package com.akash.supplychain.data.controller;

import com.akash.supplychain.data.dto.RiskOverviewResponse;
import com.akash.supplychain.data.service.DataQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data/supply-chains")
public class SupplyChainRiskController {

    private final DataQueryService dataQueryService;

    public SupplyChainRiskController(DataQueryService dataQueryService) {
        this.dataQueryService = dataQueryService;
    }

    @GetMapping("/{supplyChainId}/risks")
    public RiskOverviewResponse getRiskOverview(@PathVariable("supplyChainId") Long supplyChainId) {
        return dataQueryService.getRiskOverview(supplyChainId);
    }
}
