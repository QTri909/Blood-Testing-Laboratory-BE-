package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "quality_certificates")
public class QualityCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @Column(name = "certificate_name", nullable = false)
    private String name;

    @Column(name = "certificate_type", nullable = false)
    private String type;

    @Column(name = "certificate_number", nullable = false)
    private String certificateNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "authority", nullable = false)
    private String authority;

    @OneToMany(mappedBy = "qualityCertificate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManufacturerHasCertificate> manufacturerHasCertificate;
}
