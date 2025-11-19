package sum25.group03.patientservice.enums;

public enum MedicalRecordStatus {
    EMPTY,
    ASSIGNED, // already assign to a patient but not yet published
    FILLED, // 'ASSIGNED' and has test orders for examinations
    PUBLISHED,
    COMPLETED,
    ACTIVE,
    INACTIVE,
    DELETED,
    HIDDEN
}
