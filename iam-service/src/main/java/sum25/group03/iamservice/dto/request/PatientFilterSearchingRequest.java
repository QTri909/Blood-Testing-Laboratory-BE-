package sum25.group03.iamservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientFilterSearchingRequest {
    private String fullName;
    private String identityNumber;
    private String phoneNumber;
    private String email;

    // paging
    private int page = 0;
    private int size = 10;
}