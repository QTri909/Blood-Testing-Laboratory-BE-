package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.common.response.events.UserCreatedEvent;

public interface TestOrderKafkaProducer {
    void sendPatientInfoMessage(String key, UserCreatedEvent patientInfo);
}
