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
@Table(name = "specific_configurations")
public class SpecificConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specific_configuration_id")
    private Long specificConfigurationId;

    @Column(name = "supported_tests", nullable = false)
    private String supportedTests;

    @Column(name = "parameter_settings", nullable = false, columnDefinition = "TEXT")
    private String parameterSettings;

    @Column(name = "data_output_format", nullable = false)
    private String dataOutputFormat;

    @Column(name = "communication_protocol", nullable = false)
    private String communicationProtocol;

    @Column(name = "mixing_speed", nullable = false)
    private int  mixingSpeed;

    @Column(name = "firmware_version", nullable = false)
    private String firmwareVersion;

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

    @OneToOne(mappedBy = "specificConfiguration")
    private Instrument instrument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_configuration_id", nullable = false)
    @JsonIgnore
    private GlobalConfiguration globalConfiguration;

}
