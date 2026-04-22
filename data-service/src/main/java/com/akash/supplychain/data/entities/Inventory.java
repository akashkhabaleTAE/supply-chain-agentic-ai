package com.akash.supplychain.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "supply_chain_id", nullable = false)
    private Long supplyChainId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "warehouse_code", nullable = false, length = 40)
    private String warehouseCode;

    @Column(length = 120)
    private String location;

    @Column(name = "available_quantity", nullable = false, precision = 14, scale = 2)
    private BigDecimal availableQuantity = BigDecimal.ZERO;

    @Column(name = "reserved_quantity", nullable = false, precision = 14, scale = 2)
    private BigDecimal reservedQuantity = BigDecimal.ZERO;

    @Column(name = "reorder_point", nullable = false, precision = 14, scale = 2)
    private BigDecimal reorderPoint = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Inventory() {
    }

    public Inventory(Long supplyChainId, Material material, Supplier supplier, String warehouseCode, String location,
                     BigDecimal availableQuantity, BigDecimal reservedQuantity, BigDecimal reorderPoint) {
        this.supplyChainId = supplyChainId;
        this.material = material;
        this.supplier = supplier;
        this.warehouseCode = warehouseCode;
        this.location = location;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.reorderPoint = reorderPoint;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getSupplyChainId() {
        return supplyChainId;
    }

    public void setSupplyChainId(Long supplyChainId) {
        this.supplyChainId = supplyChainId;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(BigDecimal reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public BigDecimal getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(BigDecimal reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
