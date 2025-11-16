package sum25.group03.patientservice.helpers;

import sum25.group03.patientservice.enums.MedicalRecordStatus;

public class MedicalRecordHelper {
    public static void validatePublishMedicalRecord(MedicalRecordStatus newStatus, MedicalRecordStatus oldStatus) {
        if (newStatus != MedicalRecordStatus.PUBLISHED) {
            return;
        }
        if (oldStatus == MedicalRecordStatus.PUBLISHED)
            throw new IllegalStateException("Medical record status is already published!");
        if (oldStatus != MedicalRecordStatus.FILLED)
            throw new IllegalStateException("Only filled medical records can be published!");
    }

    public static void validateCompleteMedicalRecord(MedicalRecordStatus newStatus, MedicalRecordStatus oldStatus) {
        if (newStatus != MedicalRecordStatus.COMPLETED) {
            return;
        }
        if (oldStatus != MedicalRecordStatus.PUBLISHED)
            throw new IllegalStateException("Only published medical records can be completed!");
    }

    public static void validateAssignMedicalRecord(MedicalRecordStatus newStatus, MedicalRecordStatus oldStatus) {
        if (newStatus != MedicalRecordStatus.ASSIGNED) {
            return;
        }
        if (oldStatus != MedicalRecordStatus.EMPTY)
            throw new IllegalStateException("Only empty medical records can be assigned!");
    }

    public static void validateFillMedicalRecord(MedicalRecordStatus newStatus, MedicalRecordStatus oldStatus) {
        if (newStatus != MedicalRecordStatus.FILLED) {
            return;
        }
        if (oldStatus != MedicalRecordStatus.ASSIGNED)
            throw new IllegalStateException("Only assigned medical records can be filled!");
    }
}
