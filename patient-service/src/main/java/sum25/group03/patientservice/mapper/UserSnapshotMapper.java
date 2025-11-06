package sum25.group03.patientservice.mapper;

import org.mapstruct.*;
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
    UserFilterUpdate toUpdateInfoDTO(FeignUserDTO feignDto);
    List<UserFilterUpdate> toUpdateInfoDTOs(List<FeignUserDTO> feignDtos);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalUserId", source = "kafkaUserDTO.id")
    UserSnapshotEntity fromUserKafkaDTO(KafkaUserDTO kafkaUserDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromKafkaDTO(KafkaUserDTO kafkaUserDTO, @MappingTarget UserSnapshotEntity userSnapshotEntity);
}