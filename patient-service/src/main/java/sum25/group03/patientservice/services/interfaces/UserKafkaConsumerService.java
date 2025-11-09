package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.request.KafkaUserDTO;

public interface UserKafkaConsumerService {
    public void fetchUserFromKafka(KafkaUserDTO kafkaUserDTO);
}
