package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.entities.MedicalRecordEntity;

public interface MedicalRecordMongoService {
    void updateMedicalRecord(UpdatedAssignedDoctor updateInfo);
    void createNewMedicalRecordInMongoDb(MedicalRecordEntity medicalRecordEntity);
}
