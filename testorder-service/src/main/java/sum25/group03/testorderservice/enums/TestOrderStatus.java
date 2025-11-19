package sum25.group03.testorderservice.enums;

public enum TestOrderStatus {
    EMPTY,
    UNPUBLISHED, // has tests params but not yet published
    UNASSIGNED, // order created but not yet assigned to a Medical Record
    WAITING_PAYMENT, // already published, waiting for payment
    PENDING,
    UNMATCHED,
    ONGOING,
    WAITING,
    CANCELED,
    COMPLETED, // ready for review
    REVIEWED,
    AI_REVIEWED
}
