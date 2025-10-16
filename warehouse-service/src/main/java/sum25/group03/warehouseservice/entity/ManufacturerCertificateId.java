package sum25.group03.warehouseservice.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ManufacturerCertificateId implements Serializable {
    private Long manufacturerId;
    private Long certificateId;
}
