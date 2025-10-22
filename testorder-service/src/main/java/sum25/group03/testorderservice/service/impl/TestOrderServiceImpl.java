package sum25.group03.testorderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dto.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dto.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.entity.TestOrder;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.TestOrderMapper;
import sum25.group03.testorderservice.repository.TestOrderRepository;
import sum25.group03.testorderservice.service.interfaces.TestOrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestOrderServiceImpl implements TestOrderService {

    private final TestOrderRepository testOrderRepository;
    private final TestOrderMapper testOrderMapper;

    @Override
    public TestOrderResponseDTO createTestOrder(TestOrderRequestDTO requestDTO) {
        log.info("Creating new test order for patientId: {}", requestDTO.getPatientId());

        TestOrder testOrder = testOrderMapper.toEntity(requestDTO);
        testOrder.setStatus(TestOrderStatus.PENDING);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());

        TestOrder savedTestOrder = testOrderRepository.save(testOrder);
        log.info("Test order created successfully with id: {}", savedTestOrder.getId());

        return testOrderMapper.toResponseDto(savedTestOrder);
    }

    @Override
    public TestOrderResponseDTO updateTestOrder(Long id, TestOrderRequestDTO requestDTO) {
        log.info("Updating test order with id: {}", id);

        TestOrder existingTestOrder = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + id));

        if (existingTestOrder.getStatus() == TestOrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot update cancelled test order");
        }

        testOrderMapper.updateEntity(requestDTO, existingTestOrder);
        existingTestOrder.setUpdatedAt(LocalDateTime.now());

        TestOrder updatedTestOrder = testOrderRepository.save(existingTestOrder);
        log.info("Test order updated successfully with id: {}", updatedTestOrder.getId());

        return testOrderMapper.toResponseDto(updatedTestOrder);
    }

    @Override
    public void deleteTestOrder(Long id) {
        log.info("Deleting test order with id: {}", id);

        TestOrder testOrder = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + id));

        if (testOrder.getStatus() == TestOrderStatus.CANCELED) {
            throw new IllegalStateException("Test order has already been deleted");
        }

        if (testOrder.getStatus() == TestOrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete completed test order");
        }

        testOrder.setStatus(TestOrderStatus.CANCELED);
        testOrder.setUpdatedAt(LocalDateTime.now());

        testOrderRepository.save(testOrder);
        log.info("Test order soft deleted (cancelled) successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public TestOrderResponseDTO getTestOrderById(Long id) {
        log.info("Fetching test order with id: {}", id);

        TestOrder testOrder = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + id));

        return testOrderMapper.toResponseDto(testOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestOrderResponseDTO> getAllTestOrders() {
        log.info("Fetching all test orders");

        List<TestOrder> testOrders = testOrderRepository.findAll();
        return testOrders.stream()
                .map(testOrderMapper::toResponseDto)
                .collect(Collectors.toList());
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
    public TestOrderResponseDTO updateTestOrderStatus(Long id, TestOrderStatus status) {
        log.info("Updating test order status to {} for id: {}", status, id);

        TestOrder testOrder = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test order not found with id: " + id));

        testOrder.setStatus(status);
        testOrder.setUpdatedAt(LocalDateTime.now());

        TestOrder updatedTestOrder = testOrderRepository.save(testOrder);
        log.info("Test order status updated successfully for id: {}", id);

        return testOrderMapper.toResponseDto(updatedTestOrder);
    }
}