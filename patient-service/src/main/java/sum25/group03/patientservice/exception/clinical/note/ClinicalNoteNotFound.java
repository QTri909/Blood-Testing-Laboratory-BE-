package sum25.group03.patientservice.exception.clinical.note;

import lombok.Builder;
import sum25.group03.patientservice.exception.BusinessLogicException;

@Builder
public class ClinicalNoteNotFound extends BusinessLogicException {
    public ClinicalNoteNotFound(String message) {
        super(message, "CLINICAL_NOTE_NOT_FOUND");
    }
}
