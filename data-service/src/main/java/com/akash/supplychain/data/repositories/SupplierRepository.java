package com.akash.supplychain.data.repositories;

import com.akash.supplychain.data.entities.RiskLevel;
import com.akash.supplychain.data.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findBySupplyChainIdOrderByTierAscNameAsc(Long supplyChainId);

    List<Supplier> findBySupplyChainIdAndTierOrderByNameAsc(Long supplyChainId, Integer tier);

    List<Supplier> findBySupplyChainIdAndMaterialTypeIgnoreCaseOrderByTierAscNameAsc(Long supplyChainId, String materialType);

    List<Supplier> findBySupplyChainIdAndBaselineRiskInOrderByTierAscNameAsc(Long supplyChainId, Collection<RiskLevel> risks);

    Optional<Supplier> findBySupplierCode(String supplierCode);
}
