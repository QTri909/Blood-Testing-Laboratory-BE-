package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dto.request.TestOrderFiltering;
import sum25.group03.testorderservice.dto.request.TestOrderRequest;
import sum25.group03.testorderservice.dto.response.TestOrderResponse;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface ITestOrderService {

    TestOrderResponse createTestOrder(TestOrderRequest requestDTO);

    TestOrderResponse getTestOrderById(Long id);

    List<TestOrderResponse> getAllTestOrders();

    TestOrderResponse updateTestOrder(Long id, TestOrderRequest requestDTO);

    void deleteTestOrder(Long id);

    List<TestOrderResponse> filterTestOrders(TestOrderFiltering filterInfo);
}
