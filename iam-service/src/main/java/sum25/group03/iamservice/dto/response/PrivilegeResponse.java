package sum25.group03.iamservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivilegeResponse {
    private Long id;
    private String privilegeName;
    private String privilegeCode;
    private String privilegeDescription;
}
