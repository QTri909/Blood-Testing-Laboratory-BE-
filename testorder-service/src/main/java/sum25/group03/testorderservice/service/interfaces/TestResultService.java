package sum25.group03.testorderservice.service.interfaces;

import sum25.group03.testorderservice.dto.request.TestResultRequestDTO;
import sum25.group03.testorderservice.dto.response.TestResultResponseDTO;

import java.util.List;

public interface TestResultService {
    TestResultResponseDTO createTestResult(TestResultRequestDTO requestDTO);

    TestResultResponseDTO getTestResultById(Long id);

    List<TestResultResponseDTO> getTestResultsByTestOrderId(Long testOrderId);

    TestResultResponseDTO updateTestResult(Long id, TestResultRequestDTO requestDTO);

    void deleteTestResult(Long id);

    List<TestResultResponseDTO> getTestResultsByInstrumentId(Long instrumentId);

    List<TestResultResponseDTO> getTestResultsByParameterId(Long parameterId);
}
