package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.request.ChangeInstrumentModeRequest;
import sum25.group03.instrumentservice.controller.request.InstallReagentRequest;
import sum25.group03.instrumentservice.controller.response.ChangeInstrumentModeResponse;
import sum25.group03.instrumentservice.controller.response.InstallReagentResponse;

public interface InstrumentService {
    ChangeInstrumentModeResponse changeInstrumentMode(ChangeInstrumentModeRequest request);
    InstallReagentResponse installReagent(InstallReagentRequest request);
}
