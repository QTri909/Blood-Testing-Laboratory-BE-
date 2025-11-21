package sum25.group03.testorderservice.services.interfaces;

import org.springframework.data.domain.Page;
import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;
import sum25.group03.common.response.dtos.grpc.ParameterGrpcResponse;
import sum25.group03.testorderservice.dtos.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dtos.response.*;
import sum25.group03.testorderservice.dtos.request.TestOrderFiltering;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.util.List;
import java.util.UUID;

public interface TestOrderService{

    // -------- THUYEN---------
    TestOrderResponseDTO getTestOrderById(Long id, Long viewerId);
    CleanTestOrderResponse getTestOrderByIdCleanData(Long id);

    Page<TestOrderResponseDTO> getAllTestOrders(Integer page, Integer size, Long viewerId);
    List<TestOrderResponseDTO> getAllTestOrdersByMedicalRecordId(Long medicalRecordId, Long viewerId);
    List<TestOrderResponseDTO> filterTestOrders(TestOrderFiltering filterInfo, Long viewerId);

    // -------- HUY -----------
    //3.5.1.3 Create Patient's Test Order
    TestOrderResponseDTO createTestOrder(TestOrderRequestDTO requestDTO, Long createdBy);
    //3.5.1.4 Modify Patient's Test Order
    TestOrderResponseDTO updateTestOrder(Long id, TestOrderRequestDTO requestDTO, Long updatedBy);
    //3.5.1.5 Delete Patient Test Order
    void deleteTestOrder(Long id, Long deletedBy);
    Page<TestOrderResponseDTO> getTestOrdersByPatientId(Long patientId, Integer page, Integer size, Long viewerId);
    List<TestOrderResponseDTO> getTestOrdersByStatus(TestOrderStatus status);
    TestOrderStatusUpdateResponse updateTestOrderStatus(Long id, TestOrderStatus status, Long updatedBy);
    List<TestOrderResponseDTO> getTestOrdersByCreatedBy(Long createdBy);
    TestOrderResponseForInstrument findLatestByBarcode(String barcode);
    CreationTestOrderResponse createTestOrderForExternalSystem(String barcode);

    void updateTestOrderStatusByTestOrderCode(UUID testOrderCode, TestOrderStatus testOrderStatus);
}
