package sum25.group03.iamservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity

@Table(name = "pending_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String identityNumber;
    private String address;

    @Column(name = "role_code")
    private String roleCode;

    private boolean approved = false;
    private LocalDateTime receivedAt;
}
