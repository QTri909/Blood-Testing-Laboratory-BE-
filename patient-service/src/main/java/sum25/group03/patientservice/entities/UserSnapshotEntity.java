package sum25.group03.patientservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sum25.group03.patientservice.enums.UserSnapshotGender;
import sum25.group03.patientservice.enums.UserSnapshotRole;
import sum25.group03.patientservice.enums.UserSnapshotStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_snapshot_id")
    private Long userSnapshotId;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserSnapshotRole role;

    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", length = 100)
    @Email
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserSnapshotStatus status;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "day_of_birth")
    private LocalDate dayOfBirth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private UserSnapshotGender gender;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

}
