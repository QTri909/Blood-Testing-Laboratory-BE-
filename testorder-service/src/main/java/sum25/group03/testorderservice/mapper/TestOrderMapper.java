package sum25.group03.testorderservice.mapper;

import org.mapstruct.*;
import sum25.group03.testorderservice.dtos.request.TestOrderRequest;
import sum25.group03.testorderservice.dtos.response.TestOrderResponse;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {TestResultMapper.class, CommentMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TestOrderMapper {

    // Map từ Entity → ResponseDTO (bao gồm nested testResults và comments)
    TestOrderResponse toDTO(TestOrder entity);

    // Map từ RequestDTO → Entity khi tạo mới
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    @Mapping(target = "comments", ignore = true)
    TestOrder toEntity(TestOrderRequest dto);

    // Map cập nhật dữ liệu từ DTO vào Entity hiện có
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TestOrderRequest dto, @MappingTarget TestOrder entity);


    @AfterMapping
    default void handleStatusLogic(TestOrder entity, @MappingTarget TestOrderResponse dto) {
        if (entity.getStatus() != null && !entity.getStatus().equals(TestOrderStatus.COMPLETED)) {
            dto.setTestResults(List.of()); // Rỗng nếu chưa Completed
        }
    }


}
