package sum25.group03.patientservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.patientservice.enums.UserSnapshotGender;
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

    @UpdateTimestamp
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
