package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VendorRes {
    private String vendorName;
    private String vendorCode;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;
}
