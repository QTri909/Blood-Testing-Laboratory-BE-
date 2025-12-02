package sum25.group03.warehouseservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "configurations")
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuration_id")
    private Long configurationId;

    @Column(name = "configuration_name", nullable = false)
    private String configurationName;

    @Column(name = "supported_tests", nullable = false)
    private String supportedTests;

    @Column(name = "data_output_format", nullable = false)
    private String dataOutputFormat;

    @Column(name = "communication_protocol", nullable = false)
    private String communicationProtocol;

    @Column(name = "mixing_speed", nullable = false)
    private int  mixingSpeed;

    @Column(name = "firmware_version", nullable = false)
    private String firmwareVersion;

    @Column(name = "use_per_run", nullable = true)
    private int usePerRun;

    @Column(name = "load_threshold", nullable = false)
    private Double loadThreshold;

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

    @OneToOne(mappedBy = "configuration")
    private Instrument instrument;

}
