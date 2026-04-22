package com.akash.supplychain.agent.dto;

import java.util.List;

public record RouteOptionDto(
        String routeId,
        List<RouteLegDto> legs
) {
}
