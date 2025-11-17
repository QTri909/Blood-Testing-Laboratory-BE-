package sum25.group03.warehouseservice.service.vendor;

import sum25.group03.warehouseservice.dto.request.VendorReq;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.dto.response.VendorRes;

import java.util.List;

public interface VendorService {
    PageRes<VendorRes> getVendors(int page, int size);
    VendorRes createVendor(VendorReq req);
}
