package sum25.group03.testorderservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.dtos.response.TestOrderSummaryChart;
import sum25.group03.testorderservice.services.impl.ChartServiceImpl;
import sum25.group03.testorderservice.utils.DateUtils;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/charts")  // {{api_gateway}}/api/v1/charts
@RequiredArgsConstructor
public class ChartController {

    private final ChartServiceImpl chartService;

    @GetMapping("/test-orders-summary")
    public ApiResponse<List<TestOrderSummaryChart>> getTestOrdersSummary(
            @RequestParam(required = false) String fromDateStr, // yyyy-MM-dd
            @RequestParam(required = false) String toDateStr    // yyyy-MM-dd
    ) {

        LocalDate fromDate = (fromDateStr == null)
                ? DateUtils.getDateByNumberOfDaysAgo(DateUtils._15_DAYS)
                : DateUtils.getLocalDateFromYYYYMMDDStr(fromDateStr);

        LocalDate toDate = (toDateStr == null)
                ? LocalDate.now()
                : DateUtils.getLocalDateFromYYYYMMDDStr(toDateStr);

        return ApiResponse.add("Get test orders summary successfully", chartService.getTestOrdersSummary(fromDate, toDate));
    }
}
