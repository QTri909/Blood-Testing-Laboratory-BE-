package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.common.response.events.UserCreatedEvent;
import sum25.group03.testorderservice.dtos.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dtos.response.*;
import sum25.group03.testorderservice.dtos.request.TestOrderFiltering;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.enums.ActionTypeFeatures;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.grpc.PatientGrpcClient;
import sum25.group03.testorderservice.helpers.ParameterHelpers;
import sum25.group03.testorderservice.mapper.TestOrderMapper;
import sum25.group03.testorderservice.mapper.TestResultMapper;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.services.interfaces.TestOrderKafkaProducer;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;
import sum25.group03.testorderservice.specification.TestOrderSpecification;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestOrderServiceImpl implements TestOrderService {

    private final TestOrderRepository testOrderRepository;
    private final TestOrderKafkaProducer testOrderKafkaProducer;

    private final PatientGrpcClient patientGrpcClient;

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
        if (result.getType() == null && entity.getType() != null)
            result.setType(entity.getType().toString());
        result.setTotalPrice(totalPrice);
        return result;
    }

    @Override
    public Page<TestOrderResponseDTO> getAllTestOrders(Integer page, Integer size, Long viewerId) {

        // TODO 3: Verify viewerId existence in the system using todo_1

        // Log the action of viewing the test order list
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_TEST_ORDER_LIST, null);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<TestOrder> orders = repository.findAll(pageable);

        // get list of all patientId and list of all createdBy from test orders:
        List<Long> patientIds = orders.stream().map(TestOrder::getPatientId).filter(Objects::nonNull).toList();
        List<Long> creatorIds = orders.stream().map(TestOrder::getCreatedBy).filter(Objects::nonNull).toList();

        // call grpc to patient service database to get map of patientId and creatorId
        // -> Map<patientId, patientName> and Map<creatorId, creatorName>
        GrpcMappingPatientAndCreatorIdResponse mappingResponse = patientGrpcClient.mappingPatientIdAndCreatorIdToTheirName(patientIds, creatorIds);

        // map to Page<TestOrderResponseDTO>
        Page<TestOrderResponseDTO> result = testOrderMapper.toResponseDtoPage(orders);

        // traverse and set patientName and creatorName for each TestOrderResponseDTO
        Map<Long, String> patientMap = mappingResponse.getMappingPatientIdToName();
        Map<Long, String> creatorMap = mappingResponse.getMappingCreatorIdToName();

        // mapping each TestOrder id with its type as tring:
        Map<Long, String> testOrderIdWithType = orders.stream().map(order -> {
            Long key = order.getId();
            String value = order.getType().toString();
            if (value == null) value = "UNKNOWN";
            return Map.entry(key, value);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (TestOrderResponseDTO dto : result) {
            Long patientId = dto.getPatientId();
            Long creatorId = dto.getCreatedBy();

            // safe patient name
            dto.setPatientName(
                    patientId != null && patientMap != null
                            ? patientMap.getOrDefault(patientId, "Unknown")
                            : "Unknown"
            );


            // safe creator name
            dto.setCreatedByName(
                    creatorId != null && creatorMap != null
                            ? creatorMap.getOrDefault(creatorId, "Unknown")
                            : "Unknown"
            );

             // set type if it's null:
             String entityType = testOrderIdWithType.get(dto.getId());
             if (dto.getType() == null)
                dto.setType(entityType);
        }

        return result;
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

        if (requestDTO.getPatientInfo() == null)
            throw new IllegalArgumentException("Patient info must be provided in the test order request");

        // get patientInfo from requestDTO
        UserCreatedEvent patientInfo = requestDTO.getPatientInfo();
        Long patientId = Long.parseLong(patientInfo.getId());


        Long medicalRecordId = requestDTO.getExternalMedicalRecordId();
        if (medicalRecordId == null) {
            // create a new medical record via patient service through grpc and set the id
            Long createdMedicalRecordId = patientGrpcClient.createdMedicalRecordResponse(createdBy, patientId)
                    .getRecordId();
            requestDTO.setExternalMedicalRecordId(createdMedicalRecordId);
        }

        // map requestDTO to entity
        TestOrder testOrder = testOrderMapper.toEntity(requestDTO);
        if (patientInfo.getId() != null) {
            testOrder.setPatientId(patientId);
        }
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
    public Page<TestOrderResponseDTO> getTestOrdersByPatientId(Long patientId, Integer page, Integer size, Long viewerId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // get all test orders by patientId with pagination
        Page<TestOrder> testOrders = testOrderRepository.findByPatientId(patientId, pageable);

        // map to Page<TestOrderResponseDTO>
        return testOrderMapper.toResponseDtoPage(testOrders);
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
                .orElse(null);

        if (testOrder == null) {
            return null;
        }

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
                .status(TestOrderStatus.UNMATCHED)
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