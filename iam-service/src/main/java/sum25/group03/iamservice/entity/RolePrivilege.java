package sum25.group03.iamservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_privileges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "privilege_id")
    private Privilege privilege;
}