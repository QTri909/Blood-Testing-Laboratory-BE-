package sum25.group03.testorderservice.enums;

public enum TestOrderStatus {
    EMPTY,  // can be canceled
    UNPUBLISHED, // has tests params but not yet published  , can be canceled
    UNASSIGNED, // order created but not yet assigned to a Medical Record, can be canceled
    WAITING_PAYMENT, // already published, waiting for payment, can be canceled
    PENDING,
    UNMATCHED,
    ONGOING, // can not be canceled, or must be mechanism for refund
    WAITING,
    CANCELED,
    COMPLETED, // ready for review
    REVIEWED,
    AI_REVIEWED
}
