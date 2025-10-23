package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dto.request.TestOrderFiltering;
import sum25.group03.testorderservice.dto.request.TestOrderRequest;
import sum25.group03.testorderservice.dto.response.TestOrderResponse;
import sum25.group03.testorderservice.entity.TestOrder;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.exception.ResourceNotFoundException;
import sum25.group03.testorderservice.mapper.TestOrderMapper;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.services.interfaces.ITestOrderService;
import sum25.group03.testorderservice.specification.TestOrderSpecification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class TestOrderServiceImpl implements ITestOrderService {

    private final TestOrderRepository repository;
    private final TestOrderMapper mapper;

    @Override
    public TestOrderResponse createTestOrder(TestOrderRequest requestDTO) {
        TestOrder entity = mapper.toEntity(requestDTO);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        TestOrder saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public TestOrderResponse getTestOrderById(Long id) {
        TestOrder entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestOrder not found with id " + id));
        return mapper.toDTO(entity);
    }

    @Override
    public List<TestOrderResponse> getAllTestOrders() {
        List<TestOrder> orders = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return orders.stream().map(mapper::toDTO).toList();
    }

    @Override
    public void deleteTestOrder(Long id) {
        TestOrder entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestOrder not found with id " + id));
        repository.delete(entity);
    }

    @Override
    public List<TestOrderResponse> filterTestOrders(TestOrderFiltering filterInfo) {

        Specification<TestOrder> spec =
                TestOrderSpecification.hasStatus(filterInfo.status())
                        .and(TestOrderSpecification.hasCreatedBy(filterInfo.createdBy()))
                        .and(TestOrderSpecification.hasRunBy(filterInfo.runBy()))
                        .and(TestOrderSpecification.createdBetween(filterInfo.fromDate(), filterInfo.toDate()));

        List<TestOrder> results = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
        return results.stream().map(mapper::toDTO).toList();
    }

}
