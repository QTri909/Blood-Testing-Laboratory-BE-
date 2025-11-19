package sum25.group03.instrumentservice.service;

import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;

public interface BloodTestingService {
    CleanTestOrderResponse getCleanTestOrderById(Long testOrderId);
}
