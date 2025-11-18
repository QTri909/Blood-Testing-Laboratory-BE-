package sum25.group03.instrumentservice.service.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;
import sum25.group03.instrumentservice.mappers.GrpcTestOrderMapper;
import sum25.group03.testorder.grpc.GetCleanTestOrderByIdRequest;
import sum25.group03.testorder.grpc.GrpcCleanTestOrderResponse;
import sum25.group03.testorder.grpc.TestOrderServiceGrpc;

@Service
@RequiredArgsConstructor
public class GrpcTestOrderClient {

    private final TestOrderServiceGrpc.TestOrderServiceBlockingStub testOrderServiceBlockingStub;

    /* Example data sent by test order service
    {
        "testOrderId": 1,
        "barcode": "BC-984342",
        "testResults": [
            {
                "testResultId": 47,
                "parameterId": 3,
                "parameterCode": "RBC"
            },
            {
                "testResultId": 48,
                "parameterId": 4,
                "parameterCode": "HGB"
            }
        ]
    }
     */
    public CleanTestOrderResponse getCleanTestOrderById(Long testOrderId) {
        try {

            GetCleanTestOrderByIdRequest request = GetCleanTestOrderByIdRequest.newBuilder()
                    .setTestOrderId(testOrderId).build();

            GrpcCleanTestOrderResponse response = testOrderServiceBlockingStub.getCleanTestOrderById(request);

            // map and return to CleanTestOrderResponse
            return GrpcTestOrderMapper.toCleanTestOrderResponse(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch clean test order from gRPC", e);
        }
    }
}
