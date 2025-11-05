//package sum25.group03.iamservice.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import sum25.group03.iamservice.event.ExternalEvent;
//
//@Service
//@Slf4j
//public class KafkaEventListener {
//
//    @KafkaListener(
//            topics = "instrument.config.updated",
//            groupId = "iam-service-group",
//            containerFactory = "externalEventListenerContainerFactory"
//    )
//    public void handleExternalEvent(ExternalEvent event) {
//        log.info(" Received event from Instrument Service: {}", event);
//    }
//}
