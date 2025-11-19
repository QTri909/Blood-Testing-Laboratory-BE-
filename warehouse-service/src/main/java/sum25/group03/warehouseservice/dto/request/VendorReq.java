package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VendorReq {
    @NotBlank
    private String vendorName;
    @NotBlank
    private String contactPerson;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String address;
}
