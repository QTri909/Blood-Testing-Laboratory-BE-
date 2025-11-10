package sum25.group03.testorderservice.services.interfaces;

import org.springframework.kafka.support.Acknowledgment;

import java.io.IOException;

public interface IKafkaConsumer {
    public void process(Object message);
    public void sendToQuarantine(Object message, String reason);
    public void listen(String message, Acknowledgment ack) throws IOException;
}
