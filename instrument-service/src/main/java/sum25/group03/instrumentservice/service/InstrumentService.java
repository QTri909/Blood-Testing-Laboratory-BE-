package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.request.ChangeInstrumentModeRequest;
import sum25.group03.instrumentservice.controller.request.CreateInstrumentRequest;
import sum25.group03.instrumentservice.controller.response.ChangeInstrumentModeResponse;
import sum25.group03.instrumentservice.controller.response.InstrumentResponse;

public interface InstrumentService {
    InstrumentResponse createInstrument(CreateInstrumentRequest request);

    ChangeInstrumentModeResponse changeInstrumentMode(ChangeInstrumentModeRequest request);
}
