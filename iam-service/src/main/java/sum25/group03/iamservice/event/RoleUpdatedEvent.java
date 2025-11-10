package sum25.group03.iamservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdatedEvent {
    private Long Id;
    private String roleCode;
    private String roleName;
    private String roleDescription;
    private Set<String> privileges;
}
