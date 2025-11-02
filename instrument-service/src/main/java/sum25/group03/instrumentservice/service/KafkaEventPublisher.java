package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.event.InstrumentModeChangedEvent;
import sum25.group03.instrumentservice.event.ReagentInstalledEvent;
import sum25.group03.instrumentservice.event.TestResultPublishedEvent;
import sum25.group03.instrumentservice.event.UpdateExpiryReagent;


public interface KafkaEventPublisher {
    void publishReagentInstalledEvent(ReagentInstalledEvent event);
    void publishInstrumentModeChangedEvent(InstrumentModeChangedEvent event);
    void publicExpiredReagentEvent(UpdateExpiryReagent event);
    void publishTestResultEvent(TestResultPublishedEvent event);
}
