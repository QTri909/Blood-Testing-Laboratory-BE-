package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dtos.request.TestOrderFiltering;
import sum25.group03.testorderservice.dtos.request.TestOrderRequest;
import sum25.group03.testorderservice.dtos.response.TestOrderResponse;

import java.util.List;

public interface ITestOrderService {
    TestOrderResponse createTestOrder(TestOrderRequest requestDTO);
    TestOrderResponse getTestOrderById(Long id);
    List<TestOrderResponse> getAllTestOrders();
    void deleteTestOrder(Long id);
    List<TestOrderResponse> filterTestOrders(TestOrderFiltering filterInfo);
}
