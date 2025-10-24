package sum25.group03.iamservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cognito_user_id", unique = true)
    private String cognitoUserId;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    private String fullName;

    @Column(name = "identity_number")
    private String identityNumber;

    private String gender;

    private String address;

    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts = 0;

    @Column(name = "account_locked", nullable = false)
    private Boolean accountNonLocked = true;

    @Column(name = "last_failed_at")
    private LocalDateTime lastFailedAt;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Quan hệ với UserRole
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    // Quan hệ với UserPrivilege
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPrivilege> userPrivileges = new HashSet<>();
}