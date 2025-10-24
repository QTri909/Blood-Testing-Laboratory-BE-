package sum25.group03.iamservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_privileges",
        indexes = {
                @Index(name = "idx_user_privilege_unique", columnList = "user_id, privilege_id, is_active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "privilege_id")
    private Privilege privilege;

    private Long grantedBy;
    private LocalDateTime grantedAt;
    private Boolean isActive;
    private String note;
}
