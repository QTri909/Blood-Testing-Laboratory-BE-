package sum25.group03.testorderservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.constants.KafkaConsumerVars;
import sum25.group03.testorderservice.dtos.request.ParameterRequestDTO;

@Service
@Slf4j
public class ParameterKafkaConsumerImpl {

    @KafkaListener(topics = KafkaConsumerVars.PARAMETER_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void listenParameterTopic(Object message) {
        if (message instanceof ParameterRequestDTO) {

        }
    }
}
