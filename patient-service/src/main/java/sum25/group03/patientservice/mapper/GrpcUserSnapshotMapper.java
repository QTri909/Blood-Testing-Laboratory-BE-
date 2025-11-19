package sum25.group03.patientservice.mapper;

import org.mapstruct.Mapper;
import sum25.group03.patientservice.dtos.request.GrpcMappingPatientAndCreatorIdRequest;
import sum25.group03.patientservice.dtos.response.GrpcMappingPatientAndCreatorIdResponse;
import sum25.group03.patientservice.grpc.CreatorIdList;
import sum25.group03.patientservice.grpc.MappingPatientIdAndCreatorIdToTheirNameRequest;
import sum25.group03.patientservice.grpc.MappingPatientIdAndCreatorIdToTheirNameResponse;
import sum25.group03.patientservice.grpc.PatientIdList;

import java.util.*;

@Mapper(componentModel = "spring")
public interface GrpcUserSnapshotMapper {

    // convert javaDTO to gRPC DTO
    default GrpcMappingPatientAndCreatorIdResponse fromGrpcMappingResponse(
            MappingPatientIdAndCreatorIdToTheirNameResponse grpcDto
    ) {
        return GrpcMappingPatientAndCreatorIdResponse.builder()
                .mappingPatientIdToName(grpcDto.getPatientIdToNameMap() != null
                        ? new HashMap<>(grpcDto.getPatientIdToNameMap())
                        : Collections.emptyMap())
                .mappingCreatorIdToName(grpcDto.getCreatorIdToNameMap() != null
                        ? new HashMap<>(grpcDto.getCreatorIdToNameMap())
                        : Collections.emptyMap())
                .build();

    }

    // convert gRPC DTO to javaDTO
    default MappingPatientIdAndCreatorIdToTheirNameResponse toGrpcMappingResponse(
            GrpcMappingPatientAndCreatorIdResponse javaDto
    ) {
        return MappingPatientIdAndCreatorIdToTheirNameResponse.newBuilder()
                .putAllPatientIdToName(
                        javaDto.getMappingPatientIdToName() != null ?
                                javaDto.getMappingPatientIdToName() :
                                Collections.emptyMap()
                )
                .putAllCreatorIdToName(
                        javaDto.getMappingCreatorIdToName() != null ?
                                javaDto.getMappingCreatorIdToName() :
                                Collections.emptyMap()
                )
                .build();
    }

    // grpc to grpc to javadto:
    default GrpcMappingPatientAndCreatorIdRequest fromGrpcMappingRequest(
            MappingPatientIdAndCreatorIdToTheirNameRequest grpcReq
    ) {
        List<Long> patientIds = grpcReq.hasPatientIds() ?
                new ArrayList<>(grpcReq.getPatientIds().getPatientIdsList().stream()
                        .filter(Objects::nonNull)
                        .toList())
                : new ArrayList<>();

        List<Long> creatorIds = grpcReq.hasCreatorIds() ?
                grpcReq.getCreatorIds().getCreatorIdsList().stream()
                        .filter(Objects::nonNull).toList()
                : Collections.emptyList();

        return GrpcMappingPatientAndCreatorIdRequest.builder()
                .patientIds(patientIds)
                .creatorIds(creatorIds)
                .build();
    }



    // javadto to grpc:
    default MappingPatientIdAndCreatorIdToTheirNameRequest toGrpcMappingRequest(
            GrpcMappingPatientAndCreatorIdRequest javaReq
    ) {
        return MappingPatientIdAndCreatorIdToTheirNameRequest.newBuilder()
                .setPatientIds(
                        PatientIdList.newBuilder()
                                .addAllPatientIds(javaReq.getPatientIds() != null ? javaReq.getPatientIds() : Collections.emptyList())
                )
                .setCreatorIds(
                        CreatorIdList.newBuilder()
                                .addAllCreatorIds(javaReq.getCreatorIds() != null ? javaReq.getCreatorIds() : Collections.emptyList())
                                .build()
                )
                .build();
    }


}
