package sum25.group03.testorderservice.services.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.component.Hl7Parser;
import sum25.group03.testorderservice.dtos.request.TestResultPublishedEventDTO;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.IKafkaConsumer;
import sum25.group03.testorderservice.services.interfaces.TestResultService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerImpl implements IKafkaConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Hl7Parser  hl7Parser;
    private final TestResultRepository testResultRepository;
    private final TestResultService testResultService;

    @Override
    @KafkaListener(
            topics = "test-results-hl7",
            groupId = "test-result-group",
            containerFactory = "testResultKafkaListenerFactory"
    )
    public void listen(TestResultPublishedEventDTO eventDTO, Acknowledgment ack) throws IOException {
        System.out.println("📥 Received HL7 message: " + eventDTO);
        try {
            // process(eventDTO);
            processOnNonHL7(eventDTO);
        } catch (Exception e) {
            System.err.println("Error while processing message: " + e.getMessage());
        } finally {
            ack.acknowledge();
            System.out.println("Offset acknowledged for message: " + eventDTO);
        }
    }

    private String validateMessage(Object message) {
        if (!(message instanceof String))
            throw new IllegalArgumentException("Message is not a string");
        return (String) message;
    }

    @Override
    public void process(Object message) {

        if (!(message instanceof TestResultPublishedEventDTO))
            throw new IllegalArgumentException("Message is not TestResultPublishedEventDTO");

        TestResultPublishedEventDTO dto = (TestResultPublishedEventDTO) message;

        try {
            String hl7Content = dto.getHl7Message().replace("\\n", "\r");

            if (!hl7Content.startsWith("MSH")) {
                throw new IllegalArgumentException("Invalid HL7 header");
            }

            List<TestResult> testResults = hl7Parser.parseHL7(hl7Content);
            testResultRepository.saveAll(testResults);

        } catch (Exception e) {
            System.err.println("Parsing failed: " + e.getMessage());
            sendToQuarantine(message, e.getMessage());
        }
    }

    public void processOnNonHL7(Object message) {

        if (!(message instanceof TestResultPublishedEventDTO dto)) {
            log.error("Sync test result failed: Message is not TestResultPublishedEventDTO");
            throw new IllegalArgumentException("Sync test result failed!");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Double> resultsMap = mapper.readValue(
                    dto.getRawData(),
                    new TypeReference<Map<String, Double>>() {}
            );

            // sync to database:
            testResultService.syncTestResultsFromInstruments(resultsMap, dto);

        } catch (JsonProcessingException jpe) {
            log.error("Sync test result failed: Invalid raw data format");
            sendToQuarantine(message, jpe.getMessage());
        } catch (Exception e) {
            log.error("Sync test result failed: " + e.getMessage());
            sendToQuarantine(message, e.getMessage());
        }

    }
    /*
        private Long testOrderId;
    private Long instrumentId;
    private String barcode;
    private String rawData;
    private String hl7Message;
    private LocalDateTime timestamp;
    private String status;
     */

    @Override
    public void sendToQuarantine(Object message, String reason) {
        if (!(message instanceof TestResultPublishedEventDTO))
            throw new IllegalArgumentException("Message is not TestResultPublishedEventDTO");

        TestResultPublishedEventDTO dto = (TestResultPublishedEventDTO) message;
        String wrapped = "❌ INVALID HL7 [" + reason + "] → " + dto;
        kafkaTemplate.send("test-order-quarantine", wrapped);
        System.out.println("Sent to quarantine queue: " + wrapped);
    }
}
