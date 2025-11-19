package sum25.group03.instrumentservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;
import sum25.group03.instrumentservice.service.BloodTestingService;
import sum25.group03.instrumentservice.service.grpc.GrpcTestOrderClient;

@Service
@RequiredArgsConstructor
public class BloodTestingServiceImpl implements BloodTestingService {

    private final GrpcTestOrderClient grpcTestOrderClient;

    @Override
    public CleanTestOrderResponse getCleanTestOrderById(Long testOrderId) {

        if (testOrderId == null)
            throw new RuntimeException("testOrderId is null");

        return grpcTestOrderClient.getCleanTestOrderById(testOrderId);
    }
}
