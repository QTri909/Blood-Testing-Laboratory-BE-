package sum25.group03.testorderservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GrpcUserInfo implements Serializable {
    private Long externalUserId;
    private String fullName;
    private String email;
    private List<String> roles;
}
