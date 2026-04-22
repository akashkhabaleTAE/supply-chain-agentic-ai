package com.akash.supplychain.data.config;

import com.akash.supplychain.data.entities.Inventory;
import com.akash.supplychain.data.entities.Material;
import com.akash.supplychain.data.entities.RiskEvent;
import com.akash.supplychain.data.entities.RiskEventStatus;
import com.akash.supplychain.data.entities.RiskEventType;
import com.akash.supplychain.data.entities.RiskLevel;
import com.akash.supplychain.data.entities.Supplier;
import com.akash.supplychain.data.repositories.InventoryRepository;
import com.akash.supplychain.data.repositories.MaterialRepository;
import com.akash.supplychain.data.repositories.RiskEventRepository;
import com.akash.supplychain.data.repositories.SupplierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeedDataLoader implements CommandLineRunner {

    private static final Long DEFAULT_SUPPLY_CHAIN_ID = 1L;

    private final SupplierRepository supplierRepository;
    private final MaterialRepository materialRepository;
    private final RiskEventRepository riskEventRepository;
    private final InventoryRepository inventoryRepository;

    public SeedDataLoader(SupplierRepository supplierRepository, MaterialRepository materialRepository,
                          RiskEventRepository riskEventRepository, InventoryRepository inventoryRepository) {
        this.supplierRepository = supplierRepository;
        this.materialRepository = materialRepository;
        this.riskEventRepository = riskEventRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (supplierRepository.count() > 0) {
            return;
        }

        Material semiconductor = materialRepository.save(new Material(
                "MAT-CHIP-7NM",
                "7nm Automotive Controller Chip",
                "Semiconductor",
                "units",
                RiskLevel.CRITICAL,
                BigDecimal.valueOf(1200),
                18
        ));
        Material batteryCell = materialRepository.save(new Material(
                "MAT-LITH-CELL",
                "Lithium Ion Battery Cell",
                "Battery",
                "cells",
                RiskLevel.HIGH,
                BigDecimal.valueOf(850),
                20
        ));
        Material display = materialRepository.save(new Material(
                "MAT-OLED-6",
                "6 Inch OLED Display",
                "Display",
                "units",
                RiskLevel.MEDIUM,
                BigDecimal.valueOf(600),
                12
        ));
        Material resin = materialRepository.save(new Material(
                "MAT-RESIN-ABS",
                "ABS Resin",
                "Polymer",
                "kg",
                RiskLevel.MEDIUM,
                BigDecimal.valueOf(4200),
                15
        ));

        Supplier tsmc = supplierRepository.save(new Supplier(
                DEFAULT_SUPPLY_CHAIN_ID,
                "SUP-TW-TSMC-01",
                "Taiwan Precision Foundry",
                1,
                "Taiwan",
                "Hsinchu",
                "Semiconductor",
                RiskLevel.HIGH,
                BigDecimal.valueOf(94.50),
                32,
                "{\"upstream\":[\"SUP-JP-SIL-03\",\"SUP-US-EQP-07\"],\"downstream\":[\"Pune Assembly Hub\"]}"
        ));
        Supplier kyushu = supplierRepository.save(new Supplier(
                DEFAULT_SUPPLY_CHAIN_ID,
                "SUP-JP-SIL-03",
                "Kyushu Silicon Materials",
                2,
                "Japan",
                "Kyushu",
                "Semiconductor",
                RiskLevel.MEDIUM,
                BigDecimal.valueOf(91.20),
                24,
                "{\"upstream\":[\"SUP-AU-QUARTZ-11\"],\"downstream\":[\"SUP-TW-TSMC-01\"]}"
        ));
        Supplier koreaBattery = supplierRepository.save(new Supplier(
                DEFAULT_SUPPLY_CHAIN_ID,
                "SUP-KR-BATT-05",
                "Korea Energy Cells",
                1,
                "South Korea",
                "Ulsan",
                "Battery",
                RiskLevel.MEDIUM,
                BigDecimal.valueOf(88.40),
                21,
                "{\"upstream\":[\"SUP-CL-LITH-09\"],\"downstream\":[\"Pune Assembly Hub\"]}"
        ));
        Supplier shenzhenDisplay = supplierRepository.save(new Supplier(
                DEFAULT_SUPPLY_CHAIN_ID,
                "SUP-CN-OLED-02",
                "Shenzhen Display Works",
                1,
                "China",
                "Guangdong",
                "Display",
                RiskLevel.HIGH,
                BigDecimal.valueOf(83.75),
                18,
                "{\"upstream\":[\"SUP-KR-GLASS-06\"],\"downstream\":[\"Pune Assembly Hub\"]}"
        ));
        Supplier gujaratPolymer = supplierRepository.save(new Supplier(
                DEFAULT_SUPPLY_CHAIN_ID,
                "SUP-IN-POLY-04",
                "Gujarat Polymer Industries",
                1,
                "India",
                "Gujarat",
                "Polymer",
                RiskLevel.LOW,
                BigDecimal.valueOf(96.10),
                9,
                "{\"upstream\":[\"SUP-IN-PETRO-08\"],\"downstream\":[\"Pune Assembly Hub\"]}"
        ));

        inventoryRepository.saveAll(List.of(
                new Inventory(DEFAULT_SUPPLY_CHAIN_ID, semiconductor, tsmc, "PUN-WH-01", "Pune", BigDecimal.valueOf(18000), BigDecimal.valueOf(4200), BigDecimal.valueOf(15000)),
                new Inventory(DEFAULT_SUPPLY_CHAIN_ID, semiconductor, kyushu, "MUM-PORT-02", "Mumbai Port", BigDecimal.valueOf(9000), BigDecimal.valueOf(1000), BigDecimal.valueOf(7000)),
                new Inventory(DEFAULT_SUPPLY_CHAIN_ID, batteryCell, koreaBattery, "PUN-WH-01", "Pune", BigDecimal.valueOf(22000), BigDecimal.valueOf(3000), BigDecimal.valueOf(16000)),
                new Inventory(DEFAULT_SUPPLY_CHAIN_ID, display, shenzhenDisplay, "BLR-WH-03", "Bengaluru", BigDecimal.valueOf(7000), BigDecimal.valueOf(1800), BigDecimal.valueOf(6500)),
                new Inventory(DEFAULT_SUPPLY_CHAIN_ID, resin, gujaratPolymer, "PUN-WH-01", "Pune", BigDecimal.valueOf(64000), BigDecimal.valueOf(9000), BigDecimal.valueOf(30000))
        ));

        RiskEvent typhoon = new RiskEvent();
        typhoon.setSupplyChainId(DEFAULT_SUPPLY_CHAIN_ID);
        typhoon.setTitle("Typhoon warning near Taiwan semiconductor corridor");
        typhoon.setDescription("Port slowdowns and power resilience alerts may affect semiconductor shipments from Hsinchu.");
        typhoon.setLocation("Taiwan");
        typhoon.setType(RiskEventType.WEATHER);
        typhoon.setSeverity(RiskLevel.HIGH);
        typhoon.setStatus(RiskEventStatus.ANALYZED);
        typhoon.setExposureScore(78.5);
        typhoon.setSource("seed-news-feed");
        typhoon.setDetectedAt(LocalDateTime.now().minusHours(6));

        RiskEvent portDelay = new RiskEvent();
        portDelay.setSupplyChainId(DEFAULT_SUPPLY_CHAIN_ID);
        portDelay.setTitle("Container backlog reported at Shenzhen terminal");
        portDelay.setDescription("OLED display replenishment may slip by 3 to 5 days due to terminal congestion.");
        portDelay.setLocation("Shenzhen, China");
        portDelay.setType(RiskEventType.LOGISTICS);
        portDelay.setSeverity(RiskLevel.MEDIUM);
        portDelay.setStatus(RiskEventStatus.MAPPED);
        portDelay.setExposureScore(52.0);
        portDelay.setSource("seed-logistics-feed");
        portDelay.setDetectedAt(LocalDateTime.now().minusHours(18));

        riskEventRepository.saveAll(List.of(typhoon, portDelay));
    }
}
