package sum25.group03.payment_service.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.constants.KafkaConstants;
import sum25.group03.common.response.events.PaymentResultDTO;
import sum25.group03.payment_service.configs.PaymentKafkaConfig;

@Service
@RequiredArgsConstructor
public class KafkaPaymentResultProducer {

    private final KafkaTemplate<String, PaymentResultDTO> paymentResultKafkaTemplate;

    public void sendPaymentResult(String orderCode, String status, String transactionStatus) {
        PaymentResultDTO dto = PaymentResultDTO.builder()
                .orderCode(orderCode)
                .status(status)
                .transactionStatus(transactionStatus)
                .build();

        String topicName = KafkaConstants.PAYMENT_RESULT_TOPIC;
        paymentResultKafkaTemplate.send(topicName, dto);
    }
}
