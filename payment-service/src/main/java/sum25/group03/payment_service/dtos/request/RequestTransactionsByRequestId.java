package sum25.group03.payment_service.dtos.request;

public record RequestTransactionsByRequestId(
        Integer page,
        Integer size,
        String paymentRequestId,
        Long viewerId
) {
}
