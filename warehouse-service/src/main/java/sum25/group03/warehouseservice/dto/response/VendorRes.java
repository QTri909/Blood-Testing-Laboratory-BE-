package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorRes {
    private Long vendorId;
    private String vendorName;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdAt;
}
