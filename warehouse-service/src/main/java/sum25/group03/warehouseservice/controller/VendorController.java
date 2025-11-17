package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.VendorReq;
import sum25.group03.warehouseservice.service.vendor.VendorService;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    @GetMapping("")
    public ApiResponse<?> getVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(vendorService.getVendors(page, size));
    }

    @PostMapping("")
    public ApiResponse<?> createVendor(@RequestBody VendorReq req) {
        return ApiResponse.ok(vendorService.createVendor(req));
    }
}
