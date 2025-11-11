package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.request.TestOrderPatientInfo;
import sum25.group03.testorderservice.dtos.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dtos.response.*;
import sum25.group03.testorderservice.dtos.request.TestOrderFiltering;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.enums.ActionTypeFeatures;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.helpers.ParameterHelpers;
import sum25.group03.testorderservice.mapper.TestOrderMapper;
import sum25.group03.testorderservice.mapper.TestResultMapper;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.TestOrderKafkaProducer;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;
import sum25.group03.testorderservice.specification.TestOrderSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestOrderServiceImpl implements TestOrderService {

    private final TestOrderRepository testOrderRepository;
    private final TestOrderKafkaProducer testOrderKafkaProducer;

    private final TestOrderRepository repository;
    private final TestOrderMapper testOrderMapper;

    private final TestResultMapper testResultMapper;
    private final ActionLogService actionLogService;

    private final ParameterHelpers parameterHelpers;


    // -------- THUYEN--------
    // TODO 1: Write a function call to IAM service to verify viewerId exists in the system
    // If not, throw an exception and log a warning to the admin via cloudwatch logging

    @Override
    public TestOrderResponseDTO getTestOrderById(Long id, Long viewerId) {

        // TODO 2: Verify viewerId existence in the system using todo_1

        // Log the action of viewing the test order detail
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_TEST_ORDER_DETAIL, id);

        TestOrder entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestOrder not found with id " + id));

        // get all related test results and map to DTOs
        List<TestResult> relatedResults = entity.getTestResults();
        List<TestResultResponseDTO> testResultDtos = testResultMapper.toResponseDtos(relatedResults);

        // adjust parameter prices in test results
        Long totalPrice = 0L;
        Map<Long, Long> parameterPriceMap = parameterHelpers.loadParameterIdWithPriceMap();
        for (TestResultResponseDTO resultDto : testResultDtos) {
            Long parameterId = resultDto.getParameterId();
            Long price = parameterPriceMap.get(parameterId);
            totalPrice += price;
            resultDto.setPrice(price);
        }

        TestOrderResponseDTO result = testOrderMapper.toResponseDto(entity);
        result.setTestResults(testResultDtos);
        result.setTotalPrice(totalPrice);
        return result;
    }

    @Override
    public List<TestOrderResponseDTO> getAllTestOrders(Long viewerId) {

        // TODO 3: Verify viewerId existence in the system using todo_1

        // Log the action of viewing the test order list
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_TEST_ORDER_LIST, null);

        List<TestOrder> orders = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        // debug:
        for (TestOrder test: orders) {
            log.info("Test order code: {}", test.getCode());
        }

        return orders.stream().map(testOrderMapper::toResponseDto).toList();
    }

    @Override
    public List<TestOrderResponseDTO> getAllTestOrdersByMedicalRecordId(Long medicalRecordId, Long viewerId) {
        // Log the action of viewing the test order list
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_TEST_ORDER_LIST, null);

        List<TestOrder> orders = repository.findAllByExternalMedicalRecordId(medicalRecordId, Sort.by(Sort.Direction.DESC, "createdAt"));
        return testOrderMapper.toResponseDtoList(orders);
    }

    @Override
    public List<TestOrderResponseDTO> filterTestOrders(TestOrderFiltering filterInfo, Long viewerId) {

        // TODO 4: Verify viewerId existence in the system using todo_1

        // Log the action of viewing the test order list
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_TEST_ORDER_LIST, null);

        Specification<TestOrder> spec =
                TestOrderSpecification.hasStatus(filterInfo.status())
                        .and(TestOrderSpecification.hasCreatedBy(filterInfo.createdBy()))
                        .and(TestOrderSpecification.hasRunBy(filterInfo.runBy()))
                        .and(TestOrderSpecification.createdBetween(filterInfo.fromDate(), filterInfo.toDate()));

        List<TestOrder> results = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
        return results.stream().map(testOrderMapper::toResponseDto).toList();
    }

    // ------- HUY -----------
    @Override
    public TestOrderResponseDTO createTestOrder(TestOrderRequestDTO requestDTO, Long createdBy) {

        // get patientInfo from requestDTO
        TestOrderPatientInfo patientInfo = requestDTO.getPatientInfo();

        // map requestDTO to entity
        TestOrder testOrder = testOrderMapper.toEntity(requestDTO);
        testOrder.setCreatedBy(createdBy);

        // save to database
        TestOrder savedTestOrder = testOrderRepository.save(testOrder);
        actionLogService.logAction(
            createdBy,
            ActionTypeFeatures.CREATE_TEST_ORDER,
            savedTestOrder.getId()
        );

        // if patientId is null => new patient, send to kafka broker to IAM to create new patient
        // send only when persisting new test order successfully
        if (patientInfo.getId() == null)
            testOrderKafkaProducer.sendPatientInfoMessage("patient-info", patientInfo);

        return testOrderMapper.toResponseDto(savedTestOrder);
    }

    @Override
    public TestOrderResponseDTO updateTestOrder(Long id, TestOrderRequestDTO requestDTO, Long updatedBy) {
        log.info("Updating test order with id: {} by user: {}", id, updatedBy);

        TestOrder existingTestOrder = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + id));

        if (existingTestOrder.getStatus() == TestOrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot update cancelled test order");
        }

        Long originalPatientId = existingTestOrder.getPatientId();
        TestOrderStatus originalStatus = existingTestOrder.getStatus();

        testOrderMapper.updateEntity(requestDTO, existingTestOrder);
        // Cập nhật status nếu DTO có giá trị
        if (requestDTO.getStatus() != null) {
            existingTestOrder.setStatus(requestDTO.getStatus());
        }

        TestOrder updatedTestOrder = testOrderRepository.save(existingTestOrder);
        log.info("Test order updated successfully. ID: {}, UpdatedBy: {}, UpdateStatus: {}, PatientId changed: {} -> {}",
                id, updatedBy, originalPatientId, originalStatus, updatedTestOrder.getPatientId());

        return testOrderMapper.toResponseDto(updatedTestOrder);
    }

    @Override
    public void deleteTestOrder(Long id, Long deletedBy) {
        log.info("Deleting test order with id: {} by user: {}", id, deletedBy);

        TestOrder testOrder = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + id));

        if (testOrder.getStatus() == TestOrderStatus.CANCELED) {
            throw new IllegalStateException("Test order has already been deleted");
        }

        if (testOrder.getStatus() == TestOrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete completed test order");
        }

        log.info("Audit Log - DeletedBy: {}, TestOrderId: {}, PatientId: {}, Status: {}, CreatedBy: {}",
                deletedBy, id, testOrder.getPatientId(), testOrder.getStatus(), testOrder.getCreatedBy());

        testOrder.setStatus(TestOrderStatus.CANCELED);

        testOrderRepository.save(testOrder);
        log.info("Test order soft deleted (cancelled) successfully. ID: {}, DeletedBy: {}", id, deletedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestOrderResponseDTO> getTestOrdersByPatientId(Long patientId) {
        log.info("Fetching test orders for patientId: {}", patientId);

        List<TestOrder> testOrders = testOrderRepository.findByPatientId(patientId);
        return testOrders.stream()
                .map(testOrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestOrderResponseDTO> getTestOrdersByStatus(TestOrderStatus status) {
        log.info("Fetching test orders with status: {}", status);

        List<TestOrder> testOrders = testOrderRepository.findByStatus(status);
        return testOrders.stream()
                .map(testOrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TestOrderResponseDTO updateTestOrderStatus(Long id, TestOrderStatus status, Long updatedBy) {
        log.info("Updating test order status to {} for id: {} by user: {}", status, id, updatedBy);

        TestOrder testOrder = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + id));

        TestOrderStatus oldStatus = testOrder.getStatus();
        testOrder.setStatus(status);

        TestOrder updatedTestOrder = testOrderRepository.save(testOrder);
        log.info("Test order status updated successfully. ID: {}, Status changed: {} -> {}, UpdatedBy: {}",
                id, oldStatus, status, updatedBy);

        return testOrderMapper.toResponseDto(updatedTestOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestOrderResponseDTO> getTestOrdersByCreatedBy(Long createdBy) {
        log.info("Fetching test orders created by user: {}", createdBy);

        List<TestOrder> testOrders = testOrderRepository.findByCreatedBy(createdBy);
        return testOrders.stream()
                .map(testOrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TestOrderResponseForInstrument findLatestByBarcode(String barcode) {
        TestOrder testOrder = testOrderRepository
                .findFirstByBarcodeOrderByCreatedAtDesc(barcode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy TestOrder nào cho barcode: " + barcode
                ));
        return TestOrderResponseForInstrument.builder()
                .id(testOrder.getId())
                .code(testOrder.getCode())
                .externalMedicalRecordId(testOrder.getExternalMedicalRecordId())
                .patientId(testOrder.getPatientId())
                .createdBy(testOrder.getCreatedBy())
                .runBy(testOrder.getRunBy())
                .barcode(testOrder.getBarcode())
                .runDate(testOrder.getRunDate())
                .status(testOrder.getStatus())
                .createdAt(testOrder.getCreatedAt())
                .updatedAt(testOrder.getUpdatedAt())
                .build();
    }

    @Override
    public CreationTestOrderResponse createTestOrderForExternalSystem(String barcode) {
        TestOrder newOrder = TestOrder.builder()
                .barcode(barcode)
                .build();
        TestOrder savedOrder = testOrderRepository.save(newOrder);
        return CreationTestOrderResponse.builder()
                .id(savedOrder.getId())
                .code(savedOrder.getCode())
                .barcode(savedOrder.getBarcode())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }


}