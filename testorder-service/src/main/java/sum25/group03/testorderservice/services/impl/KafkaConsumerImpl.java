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
import sum25.group03.testorderservice.mapper.TestResultMapper;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.IKafkaConsumer;

import java.io.IOException;

@Service
public class KafkaConsumerImpl implements IKafkaConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final TestResultMapper testResultMapper;

    @Autowired
    private Hl7Parser  hl7Parser;

    private static final String CRASH_FLAG_FILE = "crash-flag.txt";

    @Autowired
    private TestResultRepository testResultRepository;

    public KafkaConsumerImpl(KafkaTemplate<String, String> kafkaTemplate,  TestResultMapper testResultMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.testResultMapper = testResultMapper;
    }

    @Override
    @KafkaListener(topics = "test-order-result", groupId = "test-order")
    public void listen(String message, Acknowledgment ack) throws IOException {
        System.out.println("📥 Received HL7 message: " + message);
        try {
            process(message);
        } catch (Exception e) {
            System.err.println("❌ Error while processing message: " + e.getMessage());
        } finally {
            ack.acknowledge();
            System.out.println("📦 Offset acknowledged for message: " + message);
        }
    }

    @Override
    public void process(String message) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(message);
            String hl7Content = jsonNode.get("message").asText().replace("\\n", "\r");

            if (!hl7Content.startsWith("MSH")) {
                throw new IllegalArgumentException("Invalid HL7 header");
            }

            TestResult testResult = hl7Parser.parseHL7(hl7Content);
            testResultRepository.save(testResult);

            System.out.println("✅ Saved test result ID: " + testResult.getId());
            System.out.println("🧬 Value: " + testResult.getValue());

        } catch (Exception e) {
            System.err.println("❌ Parsing failed: " + e.getMessage());
            sendToQuarantine(message, e.getMessage());
        }
    }

    @Override
    public void sendToQuarantine(String message, String reason) {
        String wrapped = "❌ INVALID HL7 [" + reason + "] → " + message;
        kafkaTemplate.send("test-order-quaratine", wrapped);
        System.out.println("🚨 Sent to quarantine queue: " + wrapped);
    }
}
