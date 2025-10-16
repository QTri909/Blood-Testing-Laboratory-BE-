package sum25.group03.patientservice.entities;

import jakarta.persistence.*;
import lombok.*;
import sum25.group03.patientservice.enums.UserSnapshotGender;
import sum25.group03.patientservice.enums.UserSnapshotRole;
import sum25.group03.patientservice.enums.UserSnapshotStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_snapshot", uniqueConstraints = {
        @UniqueConstraint(columnNames = "external_user_id")
})
public class UserSnapshotEntity implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "external_user_id", nullable = false)
    private Long externalUserId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserSnapshotRole role;

    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserSnapshotStatus status;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserSnapshotGender gender;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    // Bidirectional relationships with MedicalRecord
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<MedicalRecordEntity> patientRecords;

    @OneToMany(mappedBy = "assignedUserDetails", fetch = FetchType.LAZY)
    private List<MedicalRecordEntity> assignedRecords;

    @OneToMany(mappedBy = "createdByUser", fetch = FetchType.LAZY)
    private List<MedicalRecordEntity> createdRecords;

    @OneToMany(mappedBy = "updatedByUser", fetch = FetchType.LAZY)
    private List<MedicalRecordEntity> updatedRecords;

    // Bidirectional relationship with ClinicalNote
    @OneToMany(mappedBy = "notedByUser", fetch = FetchType.LAZY)
    private List<ClinicalNoteEntity> authoredNotes;
}
