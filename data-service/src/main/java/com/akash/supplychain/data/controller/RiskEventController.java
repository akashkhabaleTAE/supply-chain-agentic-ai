package com.akash.supplychain.data.controller;

import com.akash.supplychain.data.dto.CreateRiskEventRequest;
import com.akash.supplychain.data.dto.RiskEventDto;
import com.akash.supplychain.data.service.DataQueryService;
import com.akash.supplychain.data.service.RiskEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
public class RiskEventController {

    private final DataQueryService dataQueryService;
    private final RiskEventService riskEventService;

    public RiskEventController(DataQueryService dataQueryService, RiskEventService riskEventService) {
        this.dataQueryService = dataQueryService;
        this.riskEventService = riskEventService;
    }

    @GetMapping("/risk-events")
    public List<RiskEventDto> findAllRiskEvents() {
        return dataQueryService.findAllRiskEvents();
    }

    @GetMapping("/supply-chains/{supplyChainId}/risk-events")
    public List<RiskEventDto> findRiskEventsBySupplyChain(@PathVariable("supplyChainId") Long supplyChainId) {
        return dataQueryService.findRiskEventsBySupplyChain(supplyChainId);
    }

    @PostMapping("/risk-events")
    @ResponseStatus(HttpStatus.CREATED)
    public RiskEventDto createRiskEvent(@Valid @RequestBody CreateRiskEventRequest request) {
        return riskEventService.createRiskEvent(request);
    }
}
