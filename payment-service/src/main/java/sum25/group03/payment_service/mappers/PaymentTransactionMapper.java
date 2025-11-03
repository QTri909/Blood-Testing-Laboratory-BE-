package sum25.group03.payment_service.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sum25.group03.payment_service.dtos.request.PaymentTransactionRequest;
import sum25.group03.payment_service.dtos.response.PaymentTransactionResponse;
import sum25.group03.payment_service.entities.PaymentTransaction;

@Mapper(componentModel = "spring", uses = {PaymentRequestMapper.class})
public interface PaymentTransactionMapper {

    PaymentTransactionResponse toResponse(PaymentTransaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentRequest", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentTransaction toEntity(PaymentTransactionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentRequest", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PaymentTransactionRequest request, @MappingTarget PaymentTransaction entity);
}