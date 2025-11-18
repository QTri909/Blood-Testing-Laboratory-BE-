package sum25.group03.testorderservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.TestResultBulkedRequestDTO;
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
        testResultRepository.save(testResult);
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

    private List<TestResult> handleAddTestResultsByListParamIds(Set<Long> addedParamIds, TestOrder testOrder,
                                                                TestResultBulkedRequestDTO requestDTO, Long creatorId
    ) {

        // debug:
        System.out.println("Added Param Ids: " + addedParamIds);

        if (addedParamIds == null) {
            throw new IllegalArgumentException("New test result ids cannot be null or empty");
        }

        if (addedParamIds.isEmpty())
            throw new IllegalArgumentException("There is no new parameter to be added");

        List<Long> listIds = addedParamIds.stream().toList();
        List<Parameter> parameters = parameterRepository.findByIdIn(listIds);  // but this helps to reduce the number of db calls

        // map parameter with its paramId;
        Map<Long, Parameter> paramMap = parameters.stream()
                .collect(Collectors.toMap(Parameter::getId, Function.identity()));

        // Check for missing IDs
        Set<Long> missing = addedParamIds.stream()
                .filter(id -> !paramMap.containsKey(id))
                .collect(Collectors.toSet());

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("These Parameter IDs do not exist: " + missing);
        }

        List<TestResult> newTestResults = addedParamIds.stream()
                .map(paramId -> {
                    return TestResult.builder().testOrder(testOrder)
                            .parameter(paramMap.get(paramId))
                            .build();
                }).toList();

        // save all new test results:
        testResultRepository.saveAll(newTestResults);

        // save new globalTestParameterId to current test order if exists and adjust status of test order:
        Long globalTestParameterId = requestDTO.getGlobalTestParameterId();
        if (globalTestParameterId != null) {
            testOrder.setGlobalTestParameterId(globalTestParameterId);
            testOrder.setStatus(TestOrderStatus.UNPUBLISHED);
        }

        // logs action:
        actionLogService.logAction(
                creatorId, ActionTypeFeatures.CREATE_BULK_TEST_RESULTS, null
        );

        return newTestResults;
    }

    private void handleRemoveTestResultsByListParamIds(Set<Long> removedParamIds, TestOrder testOrder) {
        if (removedParamIds == null || removedParamIds.isEmpty()) {
            return;
        }

        List<Long> removedIds = removedParamIds.stream().toList();
        List<TestResult> removedTestResults = testResultRepository.findByTestOrderAndParameter_IdInOrderByCreatedAtDesc(testOrder, removedIds);

        // debug:
        System.out.println("removedTestResults: ");
        removedTestResults.forEach(rs -> System.out.println(" - TestResult ID: " + rs.getId() + ", Param ID: " + rs.getParameter().getId()));

        // remove test results has the removed param ids kept by test order entity (on RAM):
        testOrder.getTestResults().removeIf(removedTestResults::contains);

        testResultRepository.deleteAll(removedTestResults);
        testResultRepository.flush(); // force immediate deletion
    }

    @Override
    @Transactional
    public List<TestResultResponseDTO> createTestResultByBulk(TestResultBulkedRequestDTO requestDTO, Long creatorId) {

        // search for the existing test order:
        Long testOrderID = requestDTO.getTestOrderId();
        TestOrder testOrder = testOrderRepository.findById(testOrderID)
                .orElseThrow(() -> new EntityNotFoundException("Test order with id " + testOrderID + " not found"));

        if (testOrder.getStatus() != TestOrderStatus.EMPTY
            && testOrder.getStatus() != TestOrderStatus.UNPUBLISHED
        ) {
            throw new RuntimeException("Only empty or unpublished test orders can be modified");
        }

        List<TestResult> newTestResults = null;
        if (testOrder.getStatus() == TestOrderStatus.UNPUBLISHED) {
            // user can change the template, and only when the test order is UNPUBLISHED:

            // fetch current TestResults from DB, not rely on in-memory testOrder.getTestResults()
            List<TestResult> currentTestResults = testResultRepository.findByTestOrderOrderByCreatedAtDesc(testOrder);

            Set<Long> existingParamIds = currentTestResults.stream().map(
                    rs -> rs.getParameter().getId()
            ).collect(Collectors.toSet());

            // get removed params, new params:
            Set<Long> newParamIds = requestDTO.getParamsId();
            Set<Long> removedParamIds = SetUtils.difference(existingParamIds, newParamIds);
            Set<Long> addedParamIds = SetUtils.difference(newParamIds, existingParamIds);

            // debug:
            System.out.println("Existing Param Ids: " + existingParamIds);
            System.out.println("New Param Ids     : " + newParamIds);
            System.out.println("Removed Param Ids : " + removedParamIds);
            System.out.println("Added Param Ids   : " + addedParamIds);

            // get test results to be removed:
            handleRemoveTestResultsByListParamIds(removedParamIds, testOrder);

            // get test results to be created:
            newTestResults = handleAddTestResultsByListParamIds(addedParamIds, testOrder, requestDTO, creatorId);
        }
        else {
            // get a proper list of parameters from the parameters is list:
            Set<Long> addedIds = requestDTO.getParamsId();

            // get test results to be created:
            newTestResults = handleAddTestResultsByListParamIds(addedIds, testOrder, requestDTO, creatorId);
        }

        // map to response dto list and return:
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