package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dto.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dto.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.entity.TestOrder;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {TestResultMapper.class, CommentMapper.class})
public interface TestOrderMapper {

    TestOrderResponseDTO toResponseDto(TestOrder testOrder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    @Mapping(target = "comments", ignore = true)
    TestOrder toEntity(TestOrderRequestDTO requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntity(TestOrderRequestDTO requestDto, @MappingTarget TestOrder testOrder);
}