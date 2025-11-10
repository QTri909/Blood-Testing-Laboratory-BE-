package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.NewRecordStatusRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import java.util.List;
import java.util.UUID;


public interface MedicalRecordService {
    MedicalRecordResponse registerMedicalRecord(Long creatorId);
    UpdatedAssignedDoctor updateAssignedDoctor(UpdatedAssignedDoctor updateInfo);
    MedicalRecordResponse getById(Long recordId, Long viewerId);
    MedicalRecordResponse getByCode(UUID recordCode);
    List<MedicalRecordResponse> getAll(Long viewerId);
    List<MedicalRecordResponse> getByPatientId(Long patientId);
    void deleteById(NewRecordStatusRequest newStatusRequest);
}