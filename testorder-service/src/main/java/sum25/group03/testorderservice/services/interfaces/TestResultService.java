package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dtos.request.TestResultBulkedRequestDTO;
import sum25.group03.testorderservice.dtos.request.TestResultRequestDTO;
import sum25.group03.testorderservice.dtos.response.TestResultResponseDTO;

import java.util.List;

public interface TestResultService {

    // tai:
    void reviewTestResult(Long testResultId, Double adjustedValue, Long reviewId);

    // huy:
    TestResultResponseDTO createTestResult(TestResultRequestDTO requestDTO);
    List<TestResultResponseDTO> createTestResultByBulk(TestResultBulkedRequestDTO requestDTO, Long creatorId);

    TestResultResponseDTO getTestResultById(Long id);
    List<TestResultResponseDTO> getTestResultsByTestOrderId(Long testOrderId);
    TestResultResponseDTO updateTestResult(Long id, TestResultRequestDTO requestDTO);
    void deleteTestResult(Long id);
    List<TestResultResponseDTO> getTestResultsByParameterId(Long parameterId);
}
