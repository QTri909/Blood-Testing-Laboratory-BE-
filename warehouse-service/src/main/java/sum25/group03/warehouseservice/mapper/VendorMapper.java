package sum25.group03.warehouseservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sum25.group03.warehouseservice.dto.request.VendorReq;
import sum25.group03.warehouseservice.dto.response.VendorRes;
import sum25.group03.warehouseservice.entity.Vendors;

@Mapper(componentModel = "spring")
public interface VendorMapper {
    VendorRes toDto(Vendors vendor);
    @Mapping(target = "active", constant = "true")
    Vendors toEntity(VendorReq vendorRes);
}
