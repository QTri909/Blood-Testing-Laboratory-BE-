package sum25.group03.warehouseservice.service.vendor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.request.VendorReq;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.dto.response.VendorRes;
import sum25.group03.warehouseservice.entity.Vendors;
import sum25.group03.warehouseservice.mapper.VendorMapper;
import sum25.group03.warehouseservice.repository.VendorRepo;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {
    private final VendorRepo vendorRepo;
    private final VendorMapper vendorMapper;

    @Override
    public PageRes<VendorRes> getVendors(int page, int size) {
        Page<Vendors> vendors = vendorRepo.findAllByActive(true,PageRequest.of(page,size));
        List<VendorRes> vendorRes = new ArrayList<>();
        if (!vendors.isEmpty()) {
            vendorRes = vendors.stream()
                    .map(vendor -> VendorRes.builder()
                            .vendorId(vendor.getVendorId())
                            .vendorName(vendor.getVendorName())
                            .contactPerson(vendor.getContactPerson())
                            .email(vendor.getEmail())
                            .phoneNumber(vendor.getPhoneNumber())
                            .address(vendor.getAddress())
                            .createdAt(vendor.getCreatedAt())
                            .build())
                    .toList();
        }
        return PageRes.<VendorRes>builder()
                .content(vendorRes)
                .pageNumber(vendors.getNumber())
                .pageSize(vendors.getSize())
                .totalElements(vendors.getTotalElements())
                .totalPages(vendors.getTotalPages())
                .build();
    }

    @Override
    public VendorRes createVendor(VendorReq req) {
        Vendors vendor = vendorMapper.toEntity(req);
        Vendors savedVendor = vendorRepo.save(vendor);
        log.info("Created new vendor with ID: {}", savedVendor.getVendorId());
        return vendorMapper.toDto(savedVendor);
    }
}
