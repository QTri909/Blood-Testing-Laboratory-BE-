package sum25.group03.iamservice.event;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreatedEvent {
    private Long Id;
    private String roleCode;
    private String roleName;
    private String roleDescription;
    private Set<String> privileges;
}
