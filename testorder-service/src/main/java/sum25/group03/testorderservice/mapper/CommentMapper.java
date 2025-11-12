package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dtos.request.CommentRequestDTO;
import sum25.group03.testorderservice.dtos.response.CommentResponseDTO;
import sum25.group03.testorderservice.entities.Comment;
import sum25.group03.testorderservice.grpc.CommentResponse;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    // Map từ entity → response DTO
    @Mapping(source = "testOrder.id", target = "testOrderId")
    @Mapping(source = "testResult.id", target = "testResultId")
    CommentResponseDTO toResponseDto(Comment comment);

    // Khi map từ DTO → entity
    // Bỏ qua testOrder và testResult — để service xử lý sau
    @Mapping(target = "testOrder", ignore = true)
    @Mapping(target = "testResult", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toEntity(CommentRequestDTO requestDto);

    // Khi update entity từ DTO
    @Mapping(target = "testOrder", ignore = true)
    @Mapping(target = "testResult", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CommentRequestDTO requestDto, @MappingTarget Comment comment);
}
