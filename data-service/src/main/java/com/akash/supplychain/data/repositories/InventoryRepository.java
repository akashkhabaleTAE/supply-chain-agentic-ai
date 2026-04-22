package com.akash.supplychain.data.repositories;

import com.akash.supplychain.data.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findBySupplyChainIdOrderByWarehouseCodeAsc(Long supplyChainId);

    List<Inventory> findByMaterialIdOrderByWarehouseCodeAsc(Long materialId);

    List<Inventory> findBySupplierIdOrderByWarehouseCodeAsc(Long supplierId);
}
