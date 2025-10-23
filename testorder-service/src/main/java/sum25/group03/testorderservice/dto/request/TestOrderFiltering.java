package sum25.group03.testorderservice.dto.request;

import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;

public record TestOrderFiltering(
        TestOrderStatus status,
        Long createdBy,
        Long runBy,
        LocalDate fromDate,
        LocalDate toDate
) {
}