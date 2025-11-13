package sum25.group03.testorderservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.ReviewRequestDTO;
import sum25.group03.testorderservice.dtos.request.TestResultRequestDTO;
import sum25.group03.testorderservice.dtos.response.TestResultResponseDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.TestResultMapper;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.TestResultService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final TestResultMapper testResultMapper;
    private final TestOrderRepository testOrderRepository;

    // Tai
    @Override
    public void reviewTestResult(Long testResultId, Double adjustedValue, Long reviewId){
        TestResult testResult;
        Optional<TestResult> testResultOpt = testResultRepository.findById(testResultId);
        if (testResultOpt.isPresent()) {
            testResult = testResultOpt.get();
        } else {
            throw new EntityNotFoundException("Test result not found");
        }
        if (testResult.getStatus() != TestResultStatus.COMPLETED) {
            throw new IllegalStateException("Only completed results can be reviewed");
        }
        Parameter para = testResult.getParameter();
        Double min = para.getMin();
        Double max = para.getMax();
        if(adjustedValue != null){
            if (min != null && adjustedValue < min)
                throw new IllegalArgumentException("Value below minimum: " + min);
            if (max != null && adjustedValue > max)
                throw new IllegalArgumentException("Value exceeds maximum: " + max);
            testResult.setValue(adjustedValue);
        }
        testResult.setStatus(TestResultStatus.REVIEWED);
        testResult.setUpdatedAt(LocalDateTime.now());
        log.info("TestResult id: " + testResult.getId()
        +"\nReview id: " + reviewId
        + "\nAdjusted value: " + adjustedValue
        + "\nTimestamp: " + testResult.getUpdatedAt());
        testResultRepository.save(testResult);
    }

    @Override
    public String doctorReview(ReviewRequestDTO reviewRequestDTO, Long reviewId){
        TestResult testResult;
        Optional<TestResult> testResultOpt = testResultRepository.findById(reviewRequestDTO.getTestResultId());
        if (testResultOpt.isPresent()) {
            testResult = testResultOpt.get();
        } else {
            throw new EntityNotFoundException("Test result not found");
        }
        if (testResult.getStatus() != TestResultStatus.COMPLETED) {
            throw new IllegalStateException("Only completed results can be reviewed");
        }
        testResult.setReview(reviewRequestDTO.getReview());
        testResult.setUpdatedAt(LocalDateTime.now());
        testResult.setStatus(TestResultStatus.REVIEWED);
        log.info("TestResult id: " + testResult.getId()
                +"\nReview id: " + reviewId
                + "\nReview: " + reviewRequestDTO.getReview()
                + "\nTimestamp: " + testResult.getUpdatedAt());
        testResultRepository.save(testResult);
        return testResult.getReview();
    }

    // Huy
    @Override
    public TestResultResponseDTO createTestResult(TestResultRequestDTO requestDTO) {
        log.info("Creating test result for test order id: {}", requestDTO.getTestOrderId());


//        var testOrder = testOrderRepository.findById(requestDTO.getTestOrderId())
//                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + requestDTO.getTestOrderId()));

        TestResult testResult = testResultMapper.toEntity(requestDTO);
//        testResult.setTestOrder(testOrder);
        testResult.setCreatedAt(LocalDateTime.now());
        testResult.setUpdatedAt(LocalDateTime.now());

        if (testResult.getStatus() == null) {
            testResult.setStatus(TestResultStatus.PENDING);
        }

        TestResult savedResult = testResultRepository.save(testResult);
        log.info("Test result created successfully with id: {}", savedResult.getId());

        return testResultMapper.toResponseDto(savedResult);
    }

    @Override
    @Transactional(readOnly = true)
    public TestResultResponseDTO getTestResultById(Long id) {
        log.info("Retrieving test result with id: {}", id);

        TestResult testResult = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test result not found with id: " + id));

        return testResultMapper.toResponseDto(testResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultResponseDTO> getTestResultsByTestOrderId(Long testOrderId) {
        log.info("Retrieving test results for test order id: {}", testOrderId);

        List<TestResult> testResults = testResultRepository.findByTestOrderId(testOrderId);
        return testResults.stream()
                .map(testResultMapper::toResponseDto)
                .toList();
    }

    @Override
    public TestResultResponseDTO updateTestResult(Long id, TestResultRequestDTO requestDTO) {
        log.info("Updating test result with id: {}", id);

        TestResult existingResult = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test result not found with id: " + id));

        testResultMapper.updateEntity(requestDTO, existingResult);
        existingResult.setUpdatedAt(LocalDateTime.now());

        TestResult updatedResult = testResultRepository.save(existingResult);
        log.info("Test result updated successfully with id: {}", id);

        return testResultMapper.toResponseDto(updatedResult);
    }

    @Override
    public void deleteTestResult(Long id) {
        log.info("Deleting test result with id: {}", id);

        TestResult testResult = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test result not found with id: " + id));

        testResultRepository.delete(testResult);
        log.info("Test result deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultResponseDTO> getTestResultsByInstrumentId(Long instrumentId) {
        log.info("Retrieving test results for instrument id: {}", instrumentId);

        List<TestResult> testResults = testResultRepository.findByInstrumentId(instrumentId);
        return testResults.stream()
                .map(testResultMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultResponseDTO> getTestResultsByParameterId(Long parameterId) {
        log.info("Retrieving test results for parameter id: {}", parameterId);

        List<TestResult> testResults = testResultRepository.findByParameterId(parameterId);
        return testResults.stream()
                .map(testResultMapper::toResponseDto)
                .toList();
    }
}