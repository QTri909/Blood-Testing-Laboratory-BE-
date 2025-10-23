package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dto.request.TestOrderFiltering;
import sum25.group03.testorderservice.dto.request.TestOrderRequest;
import sum25.group03.testorderservice.dto.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dto.response.TestOrderResponse;
import sum25.group03.testorderservice.dto.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.util.List;

public interface TestOrderService{

    // -------- THUYEN---------
    TestOrderResponse getTestOrderById(Long id, Long viewerId);
    List<TestOrderResponse> getAllTestOrders(Long viewerId);
    List<TestOrderResponse> filterTestOrders(TestOrderFiltering filterInfo, Long viewerId);

    // -------- HUY -----------
    //3.5.1.3 Create Patient's Test Order
    TestOrderResponseDTO createTestOrder(TestOrderRequestDTO requestDTO);
    //3.5.1.4 Modify Patient's Test Order
    TestOrderResponseDTO updateTestOrder(Long id, TestOrderRequestDTO requestDTO, Long updatedBy);
    //3.5.1.5 Delete Patient Test Order
    void deleteTestOrder(Long id, Long deletedBy);
    List<TestOrderResponseDTO> getTestOrdersByPatientId(Long patientId);
    List<TestOrderResponseDTO> getTestOrdersByStatus(TestOrderStatus status);
    TestOrderResponseDTO updateTestOrderStatus(Long id, TestOrderStatus status, Long updatedBy);
    List<TestOrderResponseDTO> getTestOrdersByCreatedBy(Long createdBy);
}
