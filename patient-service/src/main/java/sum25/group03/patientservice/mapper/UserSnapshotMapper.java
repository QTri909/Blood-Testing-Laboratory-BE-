package sum25.group03.patientservice.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import sum25.group03.patientservice.dtos.request.KafkaUserDTO;
import sum25.group03.patientservice.dtos.request.UserSnapshotRequest;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.feign.dtos.FeignUserDTO;
import sum25.group03.patientservice.feign.dtos.UserFilterUpdate;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserSnapshotMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientRecords", ignore = true)
    @Mapping(target = "assignedRecords", ignore = true)
    @Mapping(target = "createdRecords", ignore = true)
    @Mapping(target = "updatedRecords", ignore = true)
    @Mapping(target = "authoredNotes", ignore = true)
    UserSnapshotEntity toEntity(UserSnapshotRequest request);
    UserSnapshotResponse toResponse(UserSnapshotEntity entity);
    List<UserSnapshotResponse> toResponseList(List<UserSnapshotEntity> entities);
    UserFilterUpdate toUpdateInfoDTO(FeignUserDTO feignDto);
    List<UserFilterUpdate> toUpdateInfoDTOs(List<FeignUserDTO> feignDtos);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalUserId", source = "kafkaUserDTO.id")
    UserSnapshotEntity fromUserKafkaDTO(KafkaUserDTO kafkaUserDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromKafkaDTO(KafkaUserDTO kafkaUserDTO, @MappingTarget UserSnapshotEntity userSnapshotEntity);

    // convert from 'Page< UserSnapshotEntity >' to 'Page< UserSnapshotResponse >'
    default Page<UserSnapshotResponse> toResponsePage(Page<UserSnapshotEntity> entities) {
        List<UserSnapshotEntity> content = entities.getContent();

        System.out.println("Content: ");
        content.forEach(System.out::println);

        List<UserSnapshotResponse> responseList = this.toResponseList(content);

        System.out.println("Mapped Responses: ");
        responseList.forEach(System.out::println);

        return new PageImpl<>(responseList, entities.getPageable(), entities.getTotalElements());
    }
}