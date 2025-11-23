package sum25.group03.testorderservice.services.interfaces;

import org.springframework.kafka.support.Acknowledgment;
import sum25.group03.testorderservice.dtos.request.TestResultPublishedEventDTO;

import java.io.IOException;

public interface IKafkaConsumer {
    public void process(Object message);
    public void sendToQuarantine(Object message, String reason);
    public void listen(TestResultPublishedEventDTO eventDTO, Acknowledgment ack) throws IOException;
}
