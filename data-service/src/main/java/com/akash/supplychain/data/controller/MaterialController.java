package com.akash.supplychain.data.controller;

import com.akash.supplychain.data.dto.MaterialDto;
import com.akash.supplychain.data.service.DataQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data/materials")
public class MaterialController {

    private final DataQueryService dataQueryService;

    public MaterialController(DataQueryService dataQueryService) {
        this.dataQueryService = dataQueryService;
    }

    @GetMapping
    public List<MaterialDto> findAllMaterials() {
        return dataQueryService.findAllMaterials();
    }

    @GetMapping("/{materialId}")
    public MaterialDto findMaterial(@PathVariable("materialId") Long materialId) {
        return dataQueryService.findMaterial(materialId);
    }
}
