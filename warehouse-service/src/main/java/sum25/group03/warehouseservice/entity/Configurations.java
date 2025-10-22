package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.ConfigType;


import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "configurations")
public class Configurations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuration_id")
    private Long configurationId;

    @Column(name = "configuration_key", nullable = false, unique = true)
    private String configurationKey;

    @Column(name = "configuration_value", nullable = false, columnDefinition = "TEXT")
    private String configurationValue;

    @Column(name = "configuration_category", nullable = false)
    private String configurationCategory;

    @Column(name = "config_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConfigType configType;

    @Column(name = "instrument_type", nullable = false)
    private String instrumentType;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "active", nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "created_by", nullable = false)
    private int createdBy;

    @Column(name = "updated_by", nullable = false)
    private int updatedBy;

    @OneToMany(mappedBy = "configurations")
    private List<Instrument> instrument;
}
