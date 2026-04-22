package com.akash.supplychain.data.repositories;

import com.akash.supplychain.data.entities.Material;
import com.akash.supplychain.data.entities.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findBySku(String sku);

    List<Material> findByCriticalityOrderByNameAsc(RiskLevel criticality);

    List<Material> findByTypeIgnoreCaseOrderByNameAsc(String type);
}
