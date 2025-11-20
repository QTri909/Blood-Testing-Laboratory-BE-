package sum25.group03.payment_service.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import sum25.group03.payment_service.dtos.request.PaymentTransactionRequest;
import sum25.group03.payment_service.dtos.response.PaymentTransactionRes;
import sum25.group03.payment_service.dtos.response.PaymentTransactionResponse;
import sum25.group03.payment_service.entities.PaymentTransaction;

import java.util.List;

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


    @Mapping(target = "status", source = "status")
    @Mapping(target = "gatewayStatusCode", source = "gatewayStatusCode")
    @Mapping(target = "paymentRequestId", source = "paymentRequest.id")
    @Mapping(target = "id", source = "id")
    PaymentTransactionRes toTransactionResDTO(PaymentTransaction entity);

    default Page<PaymentTransactionRes> toTransactionResDTOPage(Page<PaymentTransaction> entities) {
        if (entities == null)
            return new PageImpl<>(List.of());

        // else:
        List<PaymentTransactionRes> listResponse = entities.stream().map(this::toTransactionResDTO).toList();
        return new PageImpl<>(listResponse, entities.getPageable(), entities.getTotalElements());
    }
}