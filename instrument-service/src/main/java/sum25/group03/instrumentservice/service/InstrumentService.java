package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.request.ChangeInstrumentModeRequest;
import sum25.group03.instrumentservice.controller.request.InstallReagentRequest;
import sum25.group03.instrumentservice.controller.request.UpdateReagentStatusRequest;
import sum25.group03.instrumentservice.controller.response.*;

public interface InstrumentService {
    ChangeInstrumentModeResponse changeInstrumentMode(ChangeInstrumentModeRequest request);
    InstallReagentResponse installReagent(InstallReagentRequest request);

    InstrumentResponse findInstrumentById(Long id);
    InstrumentPageResponse findAllInstruments(String keyword, String sort, String status, int page, int size);

}
