package com.akash.supplychain.data.service;

import com.akash.supplychain.data.dto.InventoryDto;
import com.akash.supplychain.data.dto.MaterialDto;
import com.akash.supplychain.data.dto.RiskEventDto;
import com.akash.supplychain.data.dto.RiskOverviewResponse;
import com.akash.supplychain.data.dto.SupplierDto;
import com.akash.supplychain.data.entities.RiskEvent;
import com.akash.supplychain.data.entities.RiskEventStatus;
import com.akash.supplychain.data.entities.RiskLevel;
import com.akash.supplychain.data.exception.ResourceNotFoundException;
import com.akash.supplychain.data.repositories.InventoryRepository;
import com.akash.supplychain.data.repositories.MaterialRepository;
import com.akash.supplychain.data.repositories.RiskEventRepository;
import com.akash.supplychain.data.repositories.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DataQueryService {

    private static final List<RiskLevel> HIGH_RISK_LEVELS = List.of(RiskLevel.HIGH, RiskLevel.CRITICAL);
    private static final List<RiskEventStatus> OPEN_STATUSES = List.of(
            RiskEventStatus.DETECTED,
            RiskEventStatus.MAPPED,
            RiskEventStatus.ANALYZED,
            RiskEventStatus.MITIGATING
    );

    private final SupplierRepository supplierRepository;
    private final MaterialRepository materialRepository;
    private final RiskEventRepository riskEventRepository;
    private final InventoryRepository inventoryRepository;

    public DataQueryService(SupplierRepository supplierRepository, MaterialRepository materialRepository,
                            RiskEventRepository riskEventRepository, InventoryRepository inventoryRepository) {
        this.supplierRepository = supplierRepository;
        this.materialRepository = materialRepository;
        this.riskEventRepository = riskEventRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<SupplierDto> findAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(SupplierDto::fromEntity)
                .toList();
    }

    public SupplierDto findSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(SupplierDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier %d was not found".formatted(supplierId)));
    }

    public List<SupplierDto> findSuppliersBySupplyChain(Long supplyChainId) {
        return supplierRepository.findBySupplyChainIdOrderByTierAscNameAsc(supplyChainId).stream()
                .map(SupplierDto::fromEntity)
                .toList();
    }

    public List<SupplierDto> findHighRiskSuppliers(Long supplyChainId) {
        return supplierRepository.findBySupplyChainIdAndBaselineRiskInOrderByTierAscNameAsc(supplyChainId, HIGH_RISK_LEVELS)
                .stream()
                .map(SupplierDto::fromEntity)
                .toList();
    }

    public List<MaterialDto> findAllMaterials() {
        return materialRepository.findAll().stream()
                .map(MaterialDto::fromEntity)
                .toList();
    }

    public MaterialDto findMaterial(Long materialId) {
        return materialRepository.findById(materialId)
                .map(MaterialDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Material %d was not found".formatted(materialId)));
    }

    public List<InventoryDto> findInventoryBySupplyChain(Long supplyChainId) {
        return inventoryRepository.findBySupplyChainIdOrderByWarehouseCodeAsc(supplyChainId).stream()
                .map(InventoryDto::fromEntity)
                .toList();
    }

    public List<RiskEventDto> findAllRiskEvents() {
        return riskEventRepository.findAll().stream()
                .map(RiskEventDto::fromEntity)
                .toList();
    }

    public List<RiskEventDto> findRiskEventsBySupplyChain(Long supplyChainId) {
        return riskEventRepository.findBySupplyChainIdOrderByDetectedAtDesc(supplyChainId).stream()
                .map(RiskEventDto::fromEntity)
                .toList();
    }

    public RiskOverviewResponse getRiskOverview(Long supplyChainId) {
        List<RiskEvent> latestEvents = riskEventRepository.findTop10BySupplyChainIdOrderByDetectedAtDesc(supplyChainId);
        List<SupplierDto> highRiskSuppliers = findHighRiskSuppliers(supplyChainId);
        long openRiskEvents = riskEventRepository.countBySupplyChainIdAndStatusIn(supplyChainId, OPEN_STATUSES);
        double maxExposureScore = latestEvents.stream()
                .mapToDouble(RiskEvent::getExposureScore)
                .max()
                .orElse(0.0);

        Map<String, Long> bySeverity = new LinkedHashMap<>();
        bySeverity.put(RiskLevel.LOW.name(), riskEventRepository.countBySupplyChainIdAndSeverity(supplyChainId, RiskLevel.LOW));
        bySeverity.put(RiskLevel.MEDIUM.name(), riskEventRepository.countBySupplyChainIdAndSeverity(supplyChainId, RiskLevel.MEDIUM));
        bySeverity.put(RiskLevel.HIGH.name(), riskEventRepository.countBySupplyChainIdAndSeverity(supplyChainId, RiskLevel.HIGH));
        bySeverity.put(RiskLevel.CRITICAL.name(), riskEventRepository.countBySupplyChainIdAndSeverity(supplyChainId, RiskLevel.CRITICAL));

        return new RiskOverviewResponse(
                supplyChainId,
                maxExposureScore,
                openRiskEvents,
                highRiskSuppliers.size(),
                bySeverity,
                latestEvents.stream().map(RiskEventDto::fromEntity).toList(),
                highRiskSuppliers
        );
    }
}
