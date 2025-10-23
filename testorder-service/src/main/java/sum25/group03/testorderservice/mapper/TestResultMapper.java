package sum25.group03.testorderservice.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sum25.group03.testorderservice.dto.TestResultDTO;
import sum25.group03.testorderservice.entity.Comment;
import sum25.group03.testorderservice.entity.ReagentUsed;
import sum25.group03.testorderservice.entity.TestResult;

import java.util.stream.Collectors;


@Component
public class TestResultMapper {

    public TestResultDTO toDTO(TestResult entity) {
        return TestResultDTO.builder()
                .id(entity.getId())
                .testOrderId(entity.getTestOrder() != null ? entity.getTestOrder().getId() : null)
                .instrumentId(entity.getInstrumentId())
                .parameterSnapshotId(entity.getParameterSnapshotId())
                .flagStatus(entity.getFlagStatus())
                .status(entity.getStatus())
                .value(entity.getValue())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .testType(entity.getTestType())
                .parameterId(entity.getParameter() != null ? entity.getParameter().getId() : null)
                .reagentUsedIds(entity.getReagentsUsed() != null
                        ? entity.getReagentsUsed().stream().map(ReagentUsed::getId).collect(Collectors.toList())
                        : null)
                .commentIds(entity.getComments() != null
                        ? entity.getComments().stream().map(Comment::getId).collect(Collectors.toList())
                        : null)
                .build();
    }

    public TestResult toEntity(TestResultDTO dto) {
        TestResult entity = new TestResult();
        entity.setId(dto.getId());
        entity.setInstrumentId(dto.getInstrumentId());
        entity.setParameterSnapshotId(dto.getParameterSnapshotId());
        entity.setFlagStatus(dto.getFlagStatus());
        entity.setStatus(dto.getStatus());
        entity.setValue(dto.getValue());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setTestType(dto.getTestType());
        return entity;
    }
}
