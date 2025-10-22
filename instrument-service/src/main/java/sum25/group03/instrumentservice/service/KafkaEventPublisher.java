package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.event.ReagentInstalledEvent;


public interface KafkaEventPublisher {
    void publishReagentInstalledEvent(ReagentInstalledEvent event);
}
