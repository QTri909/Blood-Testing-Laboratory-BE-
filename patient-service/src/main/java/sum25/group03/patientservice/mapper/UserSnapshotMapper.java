package sum25.group03.patientservice.mapper;

import org.mapstruct.*;
import sum25.group03.patientservice.dtos.request.UserSnapshotRequest;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.entities.UserSnapshotEntity;

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
}
