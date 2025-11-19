package sum25.group03.testorderservice.services.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.component.Hl7Parser;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.IKafkaConsumer;

import java.io.IOException;

@Service
public class KafkaConsumerImpl implements IKafkaConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private Hl7Parser  hl7Parser;

    @Autowired
    private TestResultRepository testResultRepository;

    public KafkaConsumerImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @KafkaListener(topics = "test-order-result", groupId = "test-order")
    public void listen(String message, Acknowledgment ack) throws IOException {
        System.out.println("📥 Received HL7 message: " + message);
        try {
            process(message);
        } catch (Exception e) {
            System.err.println("Error while processing message: " + e.getMessage());
        } finally {
            ack.acknowledge();
            System.out.println("Offset acknowledged for message: " + message);
        }
    }

    private String validateMessage(Object message) {
        if (!(message instanceof String))
            throw new IllegalArgumentException("Message is not a string");
        return (String) message;
    }

    @Override
    public void process(Object message) {

        String msgString = validateMessage(message);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(msgString);
            String hl7Content = jsonNode.get("message").asText().replace("\\n", "\r");

            if (!hl7Content.startsWith("MSH")) {
                throw new IllegalArgumentException("Invalid HL7 header");
            }

            TestResult testResult = hl7Parser.parseHL7(hl7Content);
            testResultRepository.save(testResult);

            System.out.println("Saved test result ID: " + testResult.getId());
            System.out.println("Value: " + testResult.getValue());

        } catch (Exception e) {
            System.err.println("Parsing failed: " + e.getMessage());
            sendToQuarantine(message, e.getMessage());
        }
    }

    @Override
    public void sendToQuarantine(Object message, String reason) {

        String msgStr = validateMessage(message);

        String wrapped = "❌ INVALID HL7 [" + reason + "] → " + msgStr;
        kafkaTemplate.send("test-order-quaratine", wrapped);
        System.out.println("Sent to quarantine queue: " + wrapped);
    }
}
