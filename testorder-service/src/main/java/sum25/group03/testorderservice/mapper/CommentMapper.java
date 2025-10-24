package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dtos.response.CommentResponse;
import sum25.group03.testorderservice.entities.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "testOrderId", source = "testOrder.id")
    @Mapping(target = "testResultId", source = "testResult.id")
    CommentResponse toDTO(Comment entity);
}
