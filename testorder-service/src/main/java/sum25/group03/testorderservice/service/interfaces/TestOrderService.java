package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dto.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dto.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.util.List;

public interface TestOrderService{
    TestOrderResponseDTO createTestOrder(TestOrderRequestDTO requestDTO);
    TestOrderResponseDTO updateTestOrder(Long id, TestOrderRequestDTO requestDTO);
    void deleteTestOrder(Long id);
    TestOrderResponseDTO getTestOrderById(Long id);
    List<TestOrderResponseDTO> getAllTestOrders();
    List<TestOrderResponseDTO> getTestOrdersByPatientId(Long patientId);
    List<TestOrderResponseDTO> getTestOrdersByStatus(TestOrderStatus status);
    TestOrderResponseDTO updateTestOrderStatus(Long id, TestOrderStatus status);
}
