package sum25.group03.instrumentservice.mappers;

import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;
import sum25.group03.common.response.dtos.grpc.CleanTestResultResponse;
import sum25.group03.testorder.grpc.GrpcCleanTestOrderResponse;
import sum25.group03.testorder.grpc.GrpcCleanTestResultResponse;

import java.util.List;

public class GrpcTestOrderMapper {

    // map GrpcCleanTestResultResponse to CleanTestResultResponse
    public static CleanTestResultResponse toCleanTestResultResponse(GrpcCleanTestResultResponse grpcTestResultRes) {
        return CleanTestResultResponse.builder()
                .testResultId(grpcTestResultRes.getTestResultId())
                .parameterId(grpcTestResultRes.getParameterId())
                .parameterCode(grpcTestResultRes.getParameterCode())
                .build();
    }

    // map list of GrpcCleanTestResultResponse to list of CleanTestResultResponse
    public static List<CleanTestResultResponse> toCleanTestResultResponseList(List<GrpcCleanTestResultResponse> grpcTestResultResList) {

        return grpcTestResultResList.stream()
                .map(GrpcTestOrderMapper::toCleanTestResultResponse)
                .toList();
    }

    // map GrpcCleanTestOrderResponse to CleanTestOrderResponse
    public static CleanTestOrderResponse toCleanTestOrderResponse(GrpcCleanTestOrderResponse grpcTestOrderRes) {
        return CleanTestOrderResponse.builder()
                .testOrderId(grpcTestOrderRes.getTestOrderId())
                .barcode(grpcTestOrderRes.getBarcode())
                .testResults(
                        toCleanTestResultResponseList(grpcTestOrderRes.getTestResultsList())
                )
                .build();
    }
}
