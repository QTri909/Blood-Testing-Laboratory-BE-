package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
import sum25.group03.patientservice.grpc.TestOrderResponse;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderDTO;

import java.util.List;

public interface PatientService {
    List<PatientResponseDTO> getAllPatientsWith(Integer size, Integer page);

    GrpcTestOrderDTO getLatestByPatientId(Long patientId);
}
