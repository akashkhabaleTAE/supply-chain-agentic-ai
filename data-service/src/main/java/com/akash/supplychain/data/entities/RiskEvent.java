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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_events")
public class RiskEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "supply_chain_id", nullable = false)
    private Long supplyChainId;

    @NotBlank
    @Column(nullable = false, length = 180)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 160)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RiskEventType type = RiskEventType.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel severity = RiskLevel.LOW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskEventStatus status = RiskEventStatus.DETECTED;

    @Column(name = "exposure_score", nullable = false)
    private Double exposureScore = 0.0;

    @Column(length = 80)
    private String source = "manual-api";

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public RiskEvent() {
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (detectedAt == null) {
            detectedAt = now;
        }
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RiskEventType getType() {
        return type;
    }

    public void setType(RiskEventType type) {
        this.type = type;
    }

    public RiskLevel getSeverity() {
        return severity;
    }

    public void setSeverity(RiskLevel severity) {
        this.severity = severity;
    }

    public RiskEventStatus getStatus() {
        return status;
    }

    public void setStatus(RiskEventStatus status) {
        this.status = status;
    }

    public Double getExposureScore() {
        return exposureScore;
    }

    public void setExposureScore(Double exposureScore) {
        this.exposureScore = exposureScore;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
