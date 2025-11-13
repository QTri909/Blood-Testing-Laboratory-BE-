package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.event.*;


public interface KafkaEventPublisher {
    void publishReagentInstalledEvent(ReagentInstalledEvent event);
    void publishInstrumentModeChangedEvent(InstrumentModeChangedEvent event);
    void publicExpiredReagentEvent(UpdateExpiryReagent event);
    void publishTestResultEvent(TestResultPublishedEvent event);
    void publishReagentUsageHistoryEvent(ReagentUsageHistoryEvent event);
}
