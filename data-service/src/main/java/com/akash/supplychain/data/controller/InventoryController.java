package com.akash.supplychain.data.controller;

import com.akash.supplychain.data.dto.InventoryDto;
import com.akash.supplychain.data.service.DataQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
public class InventoryController {

    private final DataQueryService dataQueryService;

    public InventoryController(DataQueryService dataQueryService) {
        this.dataQueryService = dataQueryService;
    }

    @GetMapping("/supply-chains/{supplyChainId}/inventory")
    public List<InventoryDto> findInventoryBySupplyChain(@PathVariable("supplyChainId") Long supplyChainId) {
        return dataQueryService.findInventoryBySupplyChain(supplyChainId);
    }
}
