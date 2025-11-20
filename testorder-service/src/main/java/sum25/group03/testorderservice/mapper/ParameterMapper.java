package sum25.group03.testorderservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mapstruct.*;
import sum25.group03.common.response.dtos.grpc.ParameterGrpc;
import sum25.group03.common.response.dtos.grpc.ParameterGrpcResponse;
import sum25.group03.testorder.grpc.SyncParameterRequest;
import sum25.group03.testorder.grpc.SyncParameterRequestList;
import sum25.group03.testorder.grpc.SyncParameterResponse;
import sum25.group03.testorderservice.dtos.request.KafkaParameterRequestDTO;
import sum25.group03.testorderservice.dtos.request.ParameterRequestDTO;
import sum25.group03.testorderservice.dtos.response.ParameterResponseDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.enums.ParameterGender;
import sum25.group03.testorderservice.enums.ParameterStatus;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParameterMapper {

    ParameterResponseDTO toResponseDto(Parameter parameter);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    Parameter toEntity(ParameterRequestDTO requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "testResults", ignore = true)
    void updateEntity(ParameterRequestDTO requestDto, @MappingTarget Parameter parameter);

    Parameter fromKafkaDto(KafkaParameterRequestDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromKafkaDto(KafkaParameterRequestDTO dto, @MappingTarget Parameter parameter);

    @Mapping(target = "paramCode", source = "abbreviation")
    @Mapping(target = "name", source = "parameterName")
    @Mapping(target = "abbreviation", source = "abbreviation")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "min", source = "minValue")
    @Mapping(target = "max", source = "maxValue")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "externalId", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Parameter toParameterFromParameterGrpc(ParameterGrpc parameterGrpc);

    List<Parameter> toParameterListFromParameterGrpcList(List<ParameterGrpc> parameterGrpcList);


    // from request grpc to parameter request entity
    default ParameterGrpc toParameterFromGrpc(SyncParameterRequest request) {
       return ParameterGrpc.builder()
                .id(request.getId())
                .abbreviation(request.getAbbreviation())
                .parameterName(request.getParameterName())
                .price(request.getPrice())
                .description(request.getDescription())
                .minValue(request.getMinValue())
                .maxValue(request.getMaxValue())
                .unit(request.getUnit())
                .gender(request.getGender())
                .build();
    }

    // from SyncParameterRequestList to List<ParameterGrpc>
    default List<ParameterGrpc> toParameterListFromSyncedGrpcList(SyncParameterRequestList syncedList) {
        return syncedList.getParametersList()
                .stream()
                .map(this::toParameterFromGrpc)
                .toList();
    }

    // from parameter response entity to grpc response
    default SyncParameterResponse toGrpcResponseFromParameter(ParameterGrpcResponse response) {
        return SyncParameterResponse.newBuilder()
                .setSuccess(response.getSuccess())
                .setMessage(response.getMessage())
                .build();
    }

}