package sum25.group03.patientservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clinical_note", indexes = {
    @Index(name = "clinical_note_record_id_index", columnList = "record_id")
})
public class ClinicalNoteEntity implements Serializable {

    @Id
    @Column(name = "note_id")
    private Long noteId;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", referencedColumnName = "record_id", insertable = false, updatable = false)
    private MedicalRecordEntity medicalRecord;

    @Column(name = "noted_by", nullable = false)
    private Long notedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noted_by", referencedColumnName = "external_user_id", insertable = false, updatable = false)
    private UserSnapshotEntity notedByUser;

    @Column(name = "note", nullable = false, columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
