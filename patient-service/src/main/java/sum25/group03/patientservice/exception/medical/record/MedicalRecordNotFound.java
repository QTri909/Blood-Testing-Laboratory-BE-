package sum25.group03.patientservice.exception.medical.record;

import lombok.Builder;
import sum25.group03.patientservice.exception.BusinessLogicException;

public class MedicalRecordNotFound extends BusinessLogicException {
    public MedicalRecordNotFound(String message) {
        super(message, "MEDICAL_RECORD_NOT_FOUND");
    }
    public MedicalRecordNotFound(String message, String reason) {
        super(message, reason);
    }
}
