package sum25.group03.warehouseservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.ConfigType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "global_configurations")
public class GlobalConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gobal_configuration_id")
    private Long globalConfigurationId;

    @Column(name = "sample_volume", nullable = false)
    private BigDecimal sampleVolume;

    @Column(name = "sample_volume_unit", nullable = false)
    private String sampleVolumeUnit;

    @Column(name = "max_concurrent_samples", nullable = false)
    private int maxConcurrentSamples;

    @Column(name = "default_timeout", nullable = false)
    private int defaultTimeout;

    @Column(name = "use_per_run", nullable = false)
    private int usePerRun;

    @Column(name = "active", nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "created_by", nullable = true)
    private int createdBy;

    @Column(name = "updated_by", nullable = true)
    private int updatedBy;

    @OneToMany(mappedBy = "globalConfiguration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SpecificConfiguration> specificConfigurations;
}
