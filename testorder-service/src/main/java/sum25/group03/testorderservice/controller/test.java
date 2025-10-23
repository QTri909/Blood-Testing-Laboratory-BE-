package sum25.group03.testorderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.testorderservice.service.impl.KafkaConsumerImpl;

@RestController
public class test {

    @Autowired
    private KafkaConsumerImpl kafkaConsumerImpl;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestParam String message){
        kafkaTemplate.send("test-order-result", message);
        return ResponseEntity.ok("Message sent to Kafka");
    }
}
