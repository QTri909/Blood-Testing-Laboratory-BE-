package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.request.GrpcMappingPatientAndCreatorIdRequest;
import sum25.group03.patientservice.dtos.request.UserSnapshotRequest;
import sum25.group03.patientservice.dtos.response.GrpcMappingPatientAndCreatorIdResponse;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;

import java.util.List;

public interface UserSnapshotService {

    GrpcMappingPatientAndCreatorIdResponse getGrpcMappingPatientAndCreatorName(GrpcMappingPatientAndCreatorIdRequest request);

    UserSnapshotResponse create(UserSnapshotRequest request);
    UserSnapshotResponse update(Long id, UserSnapshotRequest request);
    void delete(Long id);
    UserSnapshotResponse getById(Long id);
    UserSnapshotResponse getByExternalUserId(Long externalUserId);
    List<UserSnapshotResponse> getAll();

    String getFullNameByExternalUserId(Long externalUserId);

    // test:
    void syncUserSnapshots();
}
