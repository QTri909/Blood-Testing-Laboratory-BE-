package sum25.group03.iamservice.event;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangedEvent {
    private Long userId;
    private String changedBy;
    private String occurredAt;
}
