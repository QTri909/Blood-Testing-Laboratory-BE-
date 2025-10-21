package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;

public interface MedicalRecordService {
    MedicalRecordResponse registerMedicalRecord(MedicalRecordRequest request);
    UpdatedAssignedDoctor updateAssignedDoctor(UpdatedAssignedDoctor updateInfo);
}