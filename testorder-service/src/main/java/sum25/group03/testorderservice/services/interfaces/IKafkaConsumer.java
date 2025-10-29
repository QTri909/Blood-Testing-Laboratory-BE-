package sum25.group03.testorderservice.service.interfaces;

import org.springframework.kafka.support.Acknowledgment;

import java.io.IOException;

public interface IKafkaConsumer {
    public void process(String message);
    public void sendToQuarantine(String message, String reason);
    public void listen(String message, Acknowledgment ack) throws IOException;
}
