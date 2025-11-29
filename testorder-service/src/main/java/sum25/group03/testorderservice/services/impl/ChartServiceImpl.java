package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.dtos.response.TestOrderSummaryChart;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl {

    private final TestOrderRepository testOrderRepository;

    public List<TestOrderSummaryChart> getTestOrdersSummary() {
        return testOrderRepository.getTestOrderSummaryByType();
    }

}
