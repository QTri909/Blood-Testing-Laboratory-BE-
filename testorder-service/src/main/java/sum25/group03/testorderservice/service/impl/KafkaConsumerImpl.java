package sum25.group03.testorderservice.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.mapper.TestResultMapper;
import sum25.group03.testorderservice.service.interfaces.IKafkaConsumer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class KafkaConsumerImpl implements IKafkaConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final TestResultMapper testResultMapper;

    private static final String CRASH_FLAG_FILE = "crash-flag.txt";

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
            if (!message.startsWith("MSH")) {
                throw new IllegalArgumentException("Invalid HL7 header");
            }

            System.out.println("✅ HL7 processed successfully: " + message);

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
