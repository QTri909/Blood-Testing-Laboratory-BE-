package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.dtos.response.TestOrderSummaryChart;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.services.interfaces.ChartService;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final TestOrderRepository testOrderRepository;

    public List<TestOrderSummaryChart> getTestOrdersSummary(
            LocalDate fromDate, LocalDate toDate
    ) {

        System.out.println("From date: " + fromDate);
        System.out.println("To date: " + toDate);

        List<TestOrderSummaryChart> fullList =  testOrderRepository.getTestOrderSummaryByType();

        System.out.println("Full list: " + fullList);

        // filter by date ranger:
        return fullList.stream()
                .filter(item -> {
                    LocalDate itemDate = item.getDate().toLocalDate();
                    if (itemDate == null) return false;
                    return itemDate.isAfter(fromDate) && itemDate.isBefore(toDate);
                })
                .toList();
    }

}
