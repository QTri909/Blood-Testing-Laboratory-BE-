package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.events.UserCreatedEvent;
import sum25.group03.testorderservice.constants.KafkaVariables;
import sum25.group03.testorderservice.services.interfaces.TestOrderKafkaProducer;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestOrderKafkaProducerImpl implements TestOrderKafkaProducer {

    private final KafkaTemplate<String, UserCreatedEvent> patientInfoTemplate;

    @Override
    public void sendPatientInfoMessage(String key, UserCreatedEvent patientInfo) {
        patientInfoTemplate.send(
                KafkaVariables.TEST_ORDER_TOPIC,
                key,
                patientInfo
        );

        log.info("Send message to topic {} with key {} and patientInfo\n{}",
                KafkaVariables.TEST_ORDER_TOPIC,
                key,
                patientInfo.toString()
        );
    }
}
