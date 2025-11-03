package sum25.group03.payment_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sum25.group03.payment_service.dtos.request.PaymentProviderRequest;
import sum25.group03.payment_service.dtos.response.PaymentProviderResponse;
import sum25.group03.payment_service.entities.PaymentProvider;

@Mapper(componentModel = "spring")
public interface PaymentProviderMapper {

    PaymentProviderResponse toResponse(PaymentProvider entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentProvider toEntity(PaymentProviderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PaymentProviderRequest request, @MappingTarget PaymentProvider entity);
}