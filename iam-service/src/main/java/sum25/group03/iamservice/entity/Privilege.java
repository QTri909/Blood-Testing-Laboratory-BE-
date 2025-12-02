package sum25.group03.iamservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "privileges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "privilege_name", nullable = false)
    private String privilegeName;

    @Column(name = "privilege_code", nullable = false, unique = true)
    private String privilegeCode;

    @Column(name = "privilege_description")
    private String privilegeDescription;

    @OneToMany(mappedBy = "privilege", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePrivilege> rolePrivileges = new HashSet<>();

    @OneToMany(mappedBy = "privilege")
    private Set<UserPrivilege> userPrivileges = new HashSet<>();
}
