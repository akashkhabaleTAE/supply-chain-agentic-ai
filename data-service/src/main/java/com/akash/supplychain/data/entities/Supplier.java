package com.akash.supplychain.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "supply_chain_id", nullable = false)
    private Long supplyChainId;

    @NotBlank
    @Column(name = "supplier_code", nullable = false, unique = true, length = 40)
    private String supplierCode;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String name;

    @NotNull
    @Min(1)
    @Max(4)
    @Column(nullable = false)
    private Integer tier;

    @Column(length = 80)
    private String country;

    @Column(length = 120)
    private String region;

    @Column(name = "material_type", length = 80)
    private String materialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "baseline_risk", nullable = false, length = 20)
    private RiskLevel baselineRisk = RiskLevel.LOW;

    @Column(name = "reliability_score", precision = 5, scale = 2)
    private BigDecimal reliabilityScore = BigDecimal.valueOf(90.00);

    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays = 14;

    @Column(nullable = false)
    private boolean active = true;

    @Lob
    @Column(name = "dependency_graph", columnDefinition = "TEXT")
    private String dependencyGraphJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Supplier() {
    }

    public Supplier(Long supplyChainId, String supplierCode, String name, Integer tier, String country,
                    String region, String materialType, RiskLevel baselineRisk, BigDecimal reliabilityScore,
                    Integer leadTimeDays, String dependencyGraphJson) {
        this.supplyChainId = supplyChainId;
        this.supplierCode = supplierCode;
        this.name = name;
        this.tier = tier;
        this.country = country;
        this.region = region;
        this.materialType = materialType;
        this.baselineRisk = baselineRisk;
        this.reliabilityScore = reliabilityScore;
        this.leadTimeDays = leadTimeDays;
        this.dependencyGraphJson = dependencyGraphJson;
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

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public RiskLevel getBaselineRisk() {
        return baselineRisk;
    }

    public void setBaselineRisk(RiskLevel baselineRisk) {
        this.baselineRisk = baselineRisk;
    }

    public BigDecimal getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDependencyGraphJson() {
        return dependencyGraphJson;
    }

    public void setDependencyGraphJson(String dependencyGraphJson) {
        this.dependencyGraphJson = dependencyGraphJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
