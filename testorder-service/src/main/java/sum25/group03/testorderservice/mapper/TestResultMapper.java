package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dto.response.TestResultResponse;
import sum25.group03.testorderservice.entity.ReagentUsed;
import sum25.group03.testorderservice.entity.TestResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestResultMapper {

    @Mapping(target = "testOrderId", source = "testOrder.id")
    @Mapping(target = "parameterId", source = "parameter.id")
    @Mapping(target = "reagentUsedIds", expression = "java(mapReagents(entity.getReagentsUsed()))")
    TestResultResponse toDTO(TestResult entity);

    // Map từ List<ReagentUsed> → List<Long>
    default List<Long> mapReagents(List<ReagentUsed> reagentsUsed) {
        if (reagentsUsed == null) return List.of();
        return reagentsUsed.stream().map(ReagentUsed::getId).toList();
    }
}
