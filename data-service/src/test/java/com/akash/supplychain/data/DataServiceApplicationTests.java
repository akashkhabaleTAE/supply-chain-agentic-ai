package com.akash.supplychain.data;

import com.akash.supplychain.data.service.DataQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataServiceApplicationTests {

    @Autowired
    private DataQueryService dataQueryService;

    @Test
    void seedDataLoadsForDefaultSupplyChain() {
        assertThat(dataQueryService.findSuppliersBySupplyChain(1L)).hasSizeGreaterThanOrEqualTo(5);
        assertThat(dataQueryService.findInventoryBySupplyChain(1L)).hasSizeGreaterThanOrEqualTo(5);
        assertThat(dataQueryService.getRiskOverview(1L).latestEvents()).hasSizeGreaterThanOrEqualTo(2);
    }
}
