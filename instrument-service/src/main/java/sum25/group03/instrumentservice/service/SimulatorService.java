package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;

import java.util.concurrent.CompletableFuture;

public interface SimulatorService {

    CompletableFuture<RawTestResultResponse> startTest(BloodTestingRequest request);
}
