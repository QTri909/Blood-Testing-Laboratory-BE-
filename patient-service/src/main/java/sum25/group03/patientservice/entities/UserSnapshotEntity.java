package sum25.group03.patientservice.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "identity_number", unique = true)
    private String identityNumber;

    @Column(name = "external_user_id", nullable = false)
    private Long externalUserId;

    @Type(JsonBinaryType.class)
    @Column(name = "roles", nullable = false)
    private List<String> roles;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    // Bidirectional relationships with ESMedicalRecord
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
