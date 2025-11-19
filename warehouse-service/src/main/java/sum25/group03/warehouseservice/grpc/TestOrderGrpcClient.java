package sum25.group03.warehouseservice.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.dtos.grpc.ParameterGrpc;
import sum25.group03.warehouse.grpc.SyncParameterRequest;
import sum25.group03.warehouse.grpc.SyncParameterRequestList;
import sum25.group03.warehouse.grpc.SyncParameterResponse;
import sum25.group03.warehouse.grpc.TestOrderServiceGrpc;

import java.util.List;

import static java.util.stream.Collectors.toList;


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

        System.out.println("CUONG-CUONT");

        SyncParameterRequestList request = SyncParameterRequestList.newBuilder()
                .addAllParameters(requestList)
                .build();
        return stub.syncParameter(request);
    }
}
