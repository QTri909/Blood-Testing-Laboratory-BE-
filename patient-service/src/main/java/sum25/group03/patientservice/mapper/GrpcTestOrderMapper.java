package sum25.group03.patientservice.mapper;

import org.mapstruct.Mapper;
import sum25.group03.patientservice.grpc.TestOrderResponse;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderDTO;

@Mapper(componentModel = "spring")
public interface GrpcTestOrderMapper {
    GrpcTestOrderDTO toDto(TestOrderResponse testOrderResponse);
}
