package sum25.group03.testorderservice.enums;

public enum TestOrderStatus {
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
