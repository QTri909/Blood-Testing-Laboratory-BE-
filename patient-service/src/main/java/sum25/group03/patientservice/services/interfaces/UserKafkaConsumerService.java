package sum25.group03.patientservice.services.interfaces;

import sum25.group03.common.response.events.UserCreatedEvent;

public interface UserKafkaConsumerService {
    public void fetchUserFromKafka(UserCreatedEvent kafkaUserDTO);
}
