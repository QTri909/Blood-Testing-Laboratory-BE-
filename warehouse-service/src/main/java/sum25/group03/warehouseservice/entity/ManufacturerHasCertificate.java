package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "manufacturer_has_certificate")
public class ManufacturerHasCertificate {
    @EmbeddedId
    private ManufacturerCertificateId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("manufacturerId")
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("certificateId")
    @JoinColumn(name = "certificate_id", nullable = false)
    private QualityCertificate qualityCertificate;
}
