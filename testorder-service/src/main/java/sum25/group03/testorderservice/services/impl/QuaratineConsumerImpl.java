package sum25.group03.testorderservice.services.impl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.services.interfaces.IQuaratineConsumer;

@Service
public class QuaratineConsumerImpl implements IQuaratineConsumer {

    @KafkaListener(topics = "test-order-quaratine", groupId = "test-order")
    public void listenInvalid(String invalidMessage) {
        System.err.println("🧨 Quarantine received invalid HL7: " + invalidMessage);
    }
}
