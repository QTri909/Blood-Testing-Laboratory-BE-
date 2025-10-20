package sum25.group03.warehouseservice.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ManufacturerReq {
    private String manufacturerName;
    private String country;
    private String address;
    private String phoneNumber;
    private String email;
}
