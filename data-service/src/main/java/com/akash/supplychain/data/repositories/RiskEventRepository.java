package com.akash.supplychain.data.repositories;

import com.akash.supplychain.data.entities.RiskEvent;
import com.akash.supplychain.data.entities.RiskEventStatus;
import com.akash.supplychain.data.entities.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RiskEventRepository extends JpaRepository<RiskEvent, Long> {

    List<RiskEvent> findBySupplyChainIdOrderByDetectedAtDesc(Long supplyChainId);

    List<RiskEvent> findTop10BySupplyChainIdOrderByDetectedAtDesc(Long supplyChainId);

    List<RiskEvent> findBySupplyChainIdAndStatusInOrderByDetectedAtDesc(Long supplyChainId, Collection<RiskEventStatus> statuses);

    long countBySupplyChainIdAndStatusIn(Long supplyChainId, Collection<RiskEventStatus> statuses);

    long countBySupplyChainIdAndSeverity(Long supplyChainId, RiskLevel severity);
}
