package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dto.request.CommentRequestDTO;
import sum25.group03.testorderservice.dto.response.CommentResponseDTO;
import sum25.group03.testorderservice.entity.Comment;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "testOrder.id", target = "testOrderId")
    @Mapping(source = "testResult.id", target = "testResultId")
    CommentResponseDTO toResponseDto(Comment comment);

    @Mapping(source = "testOrderId", target = "testOrder.id")
    @Mapping(source = "testResultId", target = "testResult.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toEntity(CommentRequestDTO requestDto);

    @Mapping(source = "testOrderId", target = "testOrder.id")
    @Mapping(source = "testResultId", target = "testResult.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CommentRequestDTO requestDto, @MappingTarget Comment comment);
}