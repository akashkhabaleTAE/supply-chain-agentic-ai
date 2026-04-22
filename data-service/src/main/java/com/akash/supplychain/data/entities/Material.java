package com.akash.supplychain.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 80)
    private String type;

    @Column(name = "unit_of_measure", length = 30)
    private String unitOfMeasure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel criticality = RiskLevel.LOW;

    @Column(name = "average_daily_demand", precision = 14, scale = 2)
    private BigDecimal averageDailyDemand = BigDecimal.ZERO;

    @Column(name = "safety_stock_days")
    private Integer safetyStockDays = 10;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Material() {
    }

    public Material(String sku, String name, String type, String unitOfMeasure, RiskLevel criticality,
                    BigDecimal averageDailyDemand, Integer safetyStockDays) {
        this.sku = sku;
        this.name = name;
        this.type = type;
        this.unitOfMeasure = unitOfMeasure;
        this.criticality = criticality;
        this.averageDailyDemand = averageDailyDemand;
        this.safetyStockDays = safetyStockDays;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public RiskLevel getCriticality() {
        return criticality;
    }

    public void setCriticality(RiskLevel criticality) {
        this.criticality = criticality;
    }

    public BigDecimal getAverageDailyDemand() {
        return averageDailyDemand;
    }

    public void setAverageDailyDemand(BigDecimal averageDailyDemand) {
        this.averageDailyDemand = averageDailyDemand;
    }

    public Integer getSafetyStockDays() {
        return safetyStockDays;
    }

    public void setSafetyStockDays(Integer safetyStockDays) {
        this.safetyStockDays = safetyStockDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
