package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;

public interface SimulatorService {

    void startTest(BloodTestingRequest request);
}
