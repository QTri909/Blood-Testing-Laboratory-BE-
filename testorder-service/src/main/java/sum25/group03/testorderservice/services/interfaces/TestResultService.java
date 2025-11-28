package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dtos.request.*;
import sum25.group03.testorderservice.dtos.response.TestResultResponseDTO;
import sum25.group03.testorderservice.entities.TestResult;

import java.util.List;
import java.util.Map;

public interface TestResultService {

    // tai:
    void reviewTestResult(Long testResultId, Double adjustedValue, Long reviewId);
    String doctorReview(TestResultReviewRequestDTO dto, Long reviewId);
    // huy:
    TestResultResponseDTO createTestResult(TestResultRequestDTO requestDTO);
    List<TestResultResponseDTO> createTestResultByBulk(TestResultBulkedRequestDTO requestDTO, Long creatorId);

    TestResultResponseDTO getTestResultById(Long id);
    List<TestResultResponseDTO> getTestResultsByTestOrderId(Long testOrderId);
    TestResultResponseDTO updateTestResult(Long id, TestResultRequestDTO requestDTO);
    void deleteTestResult(Long id);
    List<TestResultResponseDTO> getTestResultsByParameterId(Long parameterId);

    void syncTestResultsFromInstruments(Map<String, Double> results, TestResultPublishedEventDTO otherInfos);
}
