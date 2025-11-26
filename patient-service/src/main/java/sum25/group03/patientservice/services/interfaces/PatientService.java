package sum25.group03.patientservice.services.interfaces;

import org.springframework.data.domain.Page;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderDTO;

import java.util.List;

public interface PatientService {
    List<PatientResponseDTO> getAllIAMPatientsWith(Integer size, Integer page);
    Page<UserSnapshotResponse> getAllPatientsWith(Integer size, Integer page);
    Page<UserSnapshotResponse> getAllExistingPatientsWith(Integer size, Integer page);

    UserSnapshotResponse getPatientByExternalUserId(Long patientId, Long viewerId);

    GrpcTestOrderDTO getLatestByPatientId(Long patientId);
}
