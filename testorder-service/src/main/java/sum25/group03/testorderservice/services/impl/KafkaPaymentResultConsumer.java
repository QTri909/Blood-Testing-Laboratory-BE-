package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.constants.KafkaConstants;
import sum25.group03.common.response.events.PaymentResultDTO;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaPaymentResultConsumer {

    private final TestOrderService testOrderService;

    @KafkaListener(
            topics = KafkaConstants.PAYMENT_RESULT_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumePaymentResult(PaymentResultDTO paymentResultDTO) {
        String orderCodeStr = paymentResultDTO.getOrderCode();
        String statusStr = paymentResultDTO.getStatus();
        String transactionStatusStr = paymentResultDTO.getTransactionStatus();
        if (orderCodeStr == null || (statusStr == null && transactionStatusStr == null))
            return;

        Set<String> acceptedStatus = Set.of("SUCCESS");
        Set<String> acceptedTransStatus = Set.of("COMPLETED", "SUCCESS");

        boolean isStatusAccepted = (statusStr != null) && (acceptedStatus.contains(statusStr));
        boolean isTransStatusAccepted = (transactionStatusStr != null) && (acceptedTransStatus.contains(transactionStatusStr));

        if (!isStatusAccepted && !isTransStatusAccepted)
            return;

        UUID orderCode = UUID.fromString(orderCodeStr);
        TestOrderStatus testOrderStatus = TestOrderStatus.ONGOING;
        testOrderService.updateTestOrderStatusByTestOrderCode(orderCode, testOrderStatus);
    }
}
