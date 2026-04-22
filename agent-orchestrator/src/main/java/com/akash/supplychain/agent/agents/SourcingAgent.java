package com.akash.supplychain.agent.agents;

import com.akash.supplychain.agent.dto.MitigationOption;
import com.akash.supplychain.agent.dto.MitigationPlan;
import com.akash.supplychain.agent.dto.SourcingAction;
import com.akash.supplychain.agent.dto.SourcingPlan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SourcingAgent {

    public SourcingPlan execute(MitigationPlan mitigationPlan) {
        AtomicInteger sequence = new AtomicInteger(1);
        List<SourcingAction> actions = mitigationPlan.mitigationOptions().stream()
                .map(option -> toAction(sequence.getAndIncrement(), option))
                .toList();

        String summary = "%d sourcing action(s) prepared; approval gates preserved for procurement control."
                .formatted(actions.size());
        return new SourcingPlan(mitigationPlan, actions, summary);
    }

    private SourcingAction toAction(int sequence, MitigationOption option) {
        String status = "INVENTORY_CONTROLLER".equalsIgnoreCase(option.approvalRequired())
                ? "AUTO_PREPARED"
                : "PENDING_APPROVAL";
        BigDecimal estimatedCost = option.estimatedCostImpact() == null ? BigDecimal.ZERO : option.estimatedCostImpact();
        return new SourcingAction(
                "SRC-" + String.format("%03d", sequence),
                option.optionType(),
                option.title(),
                status,
                estimatedCost,
                option.approvalRequired(),
                option.action()
        );
    }
}
