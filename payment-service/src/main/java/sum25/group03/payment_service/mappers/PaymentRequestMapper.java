package sum25.group03.payment_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.entities.PaymentRequest;

@Mapper(componentModel = "spring", uses = {PaymentProviderMapper.class})
public interface PaymentRequestMapper {

    PaymentRequestResponse toResponse(PaymentRequest entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentProvider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "standardCurrency", target = "currency")
    PaymentRequest toEntity(PaymentRequestRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentProvider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "standardCurrency", target = "currency")
    void updateEntity(PaymentRequestRequest request, @MappingTarget PaymentRequest entity);
}