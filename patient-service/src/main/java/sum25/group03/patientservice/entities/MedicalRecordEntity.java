package sum25.group03.patientservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medical_record", indexes = {
    @Index(name = "medical_record_patient_id_index", columnList = "patient_id")
})
public class MedicalRecordEntity implements Serializable {

    @Id
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "record_code", unique = true)
    private UUID recordCode;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "external_user_id", insertable = false, updatable = false)
    private UserSnapshotEntity patient;

    @Column(name = "assigned_user")
    private Long assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user", referencedColumnName = "external_user_id", insertable = false, updatable = false)
    private UserSnapshotEntity assignedUserDetails;

    @Column(name = "visit_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime visitDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "external_user_id", insertable = false, updatable = false)
    private UserSnapshotEntity createdByUser;

    @Column(name = "updated_by")
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "external_user_id", insertable = false, updatable = false)
    private UserSnapshotEntity updatedByUser;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClinicalNoteEntity> clinicalNotes;
}
