package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dtos.request.TestOrderPatientInfo;

public interface TestOrderKafkaProducer {
    void sendPatientInfoMessage(String key, TestOrderPatientInfo patientInfo);
}
