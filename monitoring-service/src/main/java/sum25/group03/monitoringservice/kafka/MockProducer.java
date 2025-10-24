package sum25.group03.monitoringservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MockProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private String topic = "test";
    public MockProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMockEvent() {
        String mockMessage = """
            {
                "topic": "test-service",
                "action": "TEST_CREATED",
                "message": "Test created successfully",
                "operator": "system",
                "data": {
                    "testId": "12345",
                    "testName": "HL7"
                }
            }
            """;
        kafkaTemplate.send(topic, mockMessage);
        System.out.println("Sent mock event to topic " + topic);
    }
}
