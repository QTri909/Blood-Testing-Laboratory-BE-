package sum25.group03.iamservice.event;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDeletedEvent {
    private Long Id;
    private String roleCode;
    private String roleName;
    private String roleDescription;
}
