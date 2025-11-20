package sum25.group03.warehouseservice.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.dtos.grpc.ParameterGrpc;
import sum25.group03.testorder.grpc.SyncParameterRequest;
import sum25.group03.testorder.grpc.SyncParameterRequestList;
import sum25.group03.testorder.grpc.SyncParameterResponse;
import sum25.group03.testorder.grpc.TestOrderServiceGrpc;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestOrderGrpcClient {
    @GrpcClient("test-order-service")
    private TestOrderServiceGrpc.TestOrderServiceBlockingStub stub;

    public SyncParameterResponse syncParameter(List<ParameterGrpc> parameter){
        List<SyncParameterRequest> requestList = parameter.stream()
                .map(p -> SyncParameterRequest.newBuilder()
                        .setId(p.getId())
                        .setAbbreviation(p.getAbbreviation())
                        .setParameterName(p.getParameterName())
                        .setPrice(p.getPrice())
                        .setDescription(p.getDescription())
                        .setMinValue(p.getMinValue())
                        .setMaxValue(p.getMaxValue())
                        .setUnit(p.getUnit())
                        .setGender(p.getGender())
                        .build())
                .toList();

        SyncParameterRequestList request = SyncParameterRequestList.newBuilder()
                .addAllParameters(requestList)
                .build();
        return stub.syncParameter(request);
    }
}
