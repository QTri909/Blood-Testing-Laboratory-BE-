package sum25.group03.testorderservice.enums;

public enum TestOrderStatus {
    EMPTY,
    UNASSIGNED, // order created but not yet assigned to a Medical Record
    WAITING_PAYMENT,
    PENDING,
    UNMATCHED,
    ONGOING,
    WAITING,
    CANCELED,
    COMPLETED, // ready for review
    REVIEWED,
    AI_REVIEWED
}
