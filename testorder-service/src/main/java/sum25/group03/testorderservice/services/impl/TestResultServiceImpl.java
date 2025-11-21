package sum25.group03.testorderservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.TestResultBulkedRequestDTO;
import sum25.group03.testorderservice.dtos.request.ReviewRequestDTO;
import sum25.group03.testorderservice.dtos.request.TestResultRequestDTO;
import sum25.group03.testorderservice.dtos.response.TestResultResponseDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.enums.ActionTypeFeatures;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.TestResultMapper;
import sum25.group03.testorderservice.repositories.ParameterRepository;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.TestResultService;
import sum25.group03.testorderservice.utils.SetUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final TestResultMapper testResultMapper;
    private final TestOrderRepository testOrderRepository;
    private final ParameterRepository parameterRepository;
    private final ActionLogService actionLogService;

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
        if (testResult.getStatus() != TestResultStatus.COMPLETED && testResult.getStatus() != TestResultStatus.REVIEWED && testResult.getStatus() != TestResultStatus.AI_REVIEWED) {
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
        if (testResult.getStatus() != TestResultStatus.COMPLETED && testResult.getStatus() != TestResultStatus.REVIEWED && testResult.getStatus() != TestResultStatus.AI_REVIEWED) {
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
        // search for the existing test order:
        Long testOrderID = requestDTO.getTestOrderId();

        TestOrder testOrder = testOrderRepository.findById(testOrderID)
                .orElseThrow(() -> new EntityNotFoundException("Test order with id " + testOrderID + " not found"));

        TestResult testResult = testResultMapper.toEntity(requestDTO);
        TestResult savedResult = testResultRepository.save(testResult);

        if (testOrder.getStatus() == TestOrderStatus.EMPTY) {
            testOrder.setStatus(TestOrderStatus.UNPUBLISHED);
            testOrderRepository.save(testOrder);
        }

        return testResultMapper.toResponseDto(savedResult);
    }

//    private List<TestResult> handleAddTestResultsByListParamEntities(
//            List<Parameter> parameters,
//            TestOrder testOrder,
//            TestResultBulkedRequestDTO requestDTO,
//            Long creatorId
//    ) {
//        if (parameters == null || parameters.isEmpty()) {
//            throw new IllegalArgumentException("There are no parameters to be added.");
//        }
//
//        // Map to paramCode to ensure uniqueness (optional) - by Key paramCode
//        Map<String, Parameter> paramMap = parameters.stream()
//                .collect(Collectors.toMap(Parameter::getParamCode, Function.identity()));
//
//        List<TestResult> newTestResults = paramMap.values().stream()
//                .map(p -> TestResult.builder()
//                        .testOrder(testOrder)
//                        .parameter(p)
//                        .build())
//                .toList();
//
//        // Save all new test results
//        testResultRepository.saveAll(newTestResults);
//
//        // Update globalTestParameterId if exists
//        Long globalTestParameterId = requestDTO.getGlobalTestParameterId();
//        if (globalTestParameterId != null) {
//            testOrder.setGlobalTestParameterId(globalTestParameterId);
//            testOrder.setStatus(TestOrderStatus.UNPUBLISHED);
//        }
//
//        // Log action
//        actionLogService.logAction(
//                creatorId, ActionTypeFeatures.CREATE_BULK_TEST_RESULTS, null
//        );
//
//        return newTestResults;
//    }
//
//
//    private void handleRemoveTestResultsByParameters(Set<Parameter> removedParameters, TestOrder testOrder) {
//        if (removedParameters == null || removedParameters.isEmpty()) return;
//
//        List<Long> paramIdsToRemove = removedParameters.stream()
//                .map(Parameter::getId)
//                .toList();
//
//        // Fetch TestResults for the given testOrder and parameters
//        List<TestResult> removedTestResults =
//                testResultRepository.findByTestOrderAndParameter_IdInOrderByCreatedAtDesc(testOrder, paramIdsToRemove);
//
//        // Debug:
//        System.out.println("Removed TestResults: ");
//        removedTestResults.forEach(rs ->
//                System.out.println(" - TestResult ID: " + rs.getId() + ", Param ID: " + rs.getParameter().getId())
//        );
//
//        // Remove from in-memory TestOrder
//        testOrder.getTestResults().removeIf(removedTestResults::contains);
//
//        // Delete from DB
//        testResultRepository.deleteAll(removedTestResults);
//        testResultRepository.flush();
//    }
//
//
//    @Override
//    @Transactional
//    public List<TestResultResponseDTO> createTestResultByBulk(TestResultBulkedRequestDTO requestDTO, Long creatorId) {
//
//        // search for the existing test order:
//        Long testOrderID = requestDTO.getTestOrderId();
//        TestOrder testOrder = testOrderRepository.findById(testOrderID)
//                .orElseThrow(() -> new EntityNotFoundException("Test order with id " + testOrderID + " not found"));
//
//        if (testOrder.getStatus() != TestOrderStatus.EMPTY
//            && testOrder.getStatus() != TestOrderStatus.UNPUBLISHED
//        ) {
//            throw new RuntimeException("Only empty or unpublished test orders can be modified");
//        }
//
//        List<TestResult> newTestResults = null;
//        if (testOrder.getStatus() == TestOrderStatus.UNPUBLISHED) {
//            // Fetch current TestResults from DB
//            List<TestResult> currentTestResults = testResultRepository.findByTestOrderOrderByCreatedAtDesc(testOrder);
//
//            // Map existing Parameters by externalId
//            Map<Long, Parameter> existingParamMap = currentTestResults.stream()
//                    .map(TestResult::getParameter)
//                    .collect(Collectors.toMap(Parameter::getExternalId, Function.identity(), (p1, p2) -> p1));
//            // keep first Parameter if duplicates (Uniqueness by externalId)
//
//            // Get requested externalIds from request
//            Set<Long> requestedExternalIds = requestDTO.getExternalParamIds();
//
//            // Fetch first Parameter per externalId from repository
//            List<Parameter> requestedParameters = parameterRepository.findFirstPerExternalIdByExternalIds(requestedExternalIds);
//
//            // Compute parameters to remove and add
//            Set<Parameter> removedParameters = existingParamMap.values().stream()
//                    .filter(p -> requestedExternalIds.stream().noneMatch(id -> id.equals(p.getExternalId())))
//                    .collect(Collectors.toSet());
//
//            List<Parameter> addedParameters = requestedParameters.stream()
//                    .filter(p -> !existingParamMap.containsKey(p.getExternalId()))
//                            .toList();
//
//            // Debug
//            System.out.println("Existing Params: " + existingParamMap.keySet());
//            System.out.println("Removed Params: " + removedParameters.stream().map(Parameter::getId).toList());
//            System.out.println("Added Params: " + addedParameters.stream().map(Parameter::getId).toList());
//
//            // Remove old TestResults
//            handleRemoveTestResultsByParameters(removedParameters, testOrder);
//
//            // Add new TestResults
//            newTestResults = handleAddTestResultsByListParamEntities(addedParameters, testOrder, requestDTO, creatorId);
//        }
//
//        else {
//            // get a proper list of parameters from the parameters is list:
//            Set<Long> externalIds = requestDTO.getExternalParamIds();
//
//            // get addedIds:
//            List<Parameter> addedParams = parameterRepository.findFirstPerExternalIdByExternalIds(externalIds);
//
//            // get test results to be created:
//            newTestResults = handleAddTestResultsByListParamEntities(addedParams, testOrder, requestDTO, creatorId);
//        }
//
//        // map to response dto list and return:
//        return testResultMapper.toResponseDtos(newTestResults);
//    }

    private List<Parameter> resolveRequestedParameters(Set<Long> externalIds) {
        // Always use the repository method that returns the "first" per externalId
        return parameterRepository.findFirstPerExternalIdByExternalIds(externalIds);
    }

    private void removeAllTestResults(TestOrder testOrder) {
        List<TestResult> existing = testResultRepository.findByTestOrderOrderByCreatedAtDesc(testOrder);

        if (!existing.isEmpty()) {
            testOrder.getTestResults().clear();
            testResultRepository.deleteAll(existing);
            testResultRepository.flush();
        }
    }

    private List<TestResult> createTestResults(
            List<Parameter> parameters,
            TestOrder testOrder,
            TestResultBulkedRequestDTO requestDTO,
            Long creatorId
    ) {
        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException("No parameters provided.");
        }

        // Deduplicate by paramCode (your original logic) — optional
        Map<String, Parameter> paramMap = parameters.stream()
                .collect(Collectors.toMap(Parameter::getParamCode, Function.identity(), (a, b) -> a));

        List<TestResult> testResults = paramMap.values().stream()
                .map(p -> TestResult.builder()
                        .testOrder(testOrder)
                        .parameter(p)
                        .build())
                .toList();

        testResultRepository.saveAll(testResults);

        // globalTestParameterId update (same as before)
        if (requestDTO.getGlobalTestParameterId() != null) {
            testOrder.setGlobalTestParameterId(requestDTO.getGlobalTestParameterId());
            testOrder.setStatus(TestOrderStatus.UNPUBLISHED);
        }

        actionLogService.logAction(
                creatorId, ActionTypeFeatures.CREATE_BULK_TEST_RESULTS, null
        );

        return testResults;
    }

    @Override
    @Transactional
    public List<TestResultResponseDTO> createTestResultByBulk(TestResultBulkedRequestDTO requestDTO, Long creatorId) {

        Long testOrderId = requestDTO.getTestOrderId();

        TestOrder testOrder = testOrderRepository.findById(testOrderId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Test order with id " + testOrderId + " not found")
                );

        // business rule
        if (testOrder.getStatus() != TestOrderStatus.EMPTY
                && testOrder.getStatus() != TestOrderStatus.UNPUBLISHED) {
            throw new RuntimeException("Only empty or unpublished test orders can be modified");
        }

        // Step 1: Remove all old results
        removeAllTestResults(testOrder);

        // Step 2: Resolve parameters for requested externalIds
        Set<Long> externalIds = requestDTO.getExternalParamIds();
        List<Parameter> parameters = resolveRequestedParameters(externalIds);

        // Step 3: Rebuild test results
        List<TestResult> newTestResults =
                createTestResults(parameters, testOrder, requestDTO, creatorId);

        // Return DTOs
        return testResultMapper.toResponseDtos(newTestResults);
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
    public List<TestResultResponseDTO> getTestResultsByParameterId(Long parameterId) {
        log.info("Retrieving test results for parameter id: {}", parameterId);

        List<TestResult> testResults = testResultRepository.findByParameterId(parameterId);
        return testResults.stream()
                .map(testResultMapper::toResponseDto)
                .toList();
    }
}