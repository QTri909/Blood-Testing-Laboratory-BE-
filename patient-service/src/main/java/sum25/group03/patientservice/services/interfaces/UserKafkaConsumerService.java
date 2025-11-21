package sum25.group03.patientservice.services.interfaces;

import sum25.group03.common.response.events.UserCreatedEvent;
import sum25.group03.common.response.events.UserDeletedEvent;
import sum25.group03.common.response.events.UserUpdatedEvent;

public interface UserKafkaConsumerService {
    void fetchUserFromKafka(UserCreatedEvent kafkaUserDTO);
    void fetchUpdatedUserFromKafka(UserUpdatedEvent kafkaUserDTO);
    void fetchDeletedUserFromKafka(UserDeletedEvent userDeletedEvent);
}
