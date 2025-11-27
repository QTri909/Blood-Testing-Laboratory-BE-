package sum25.group03.instrumentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuration_id")
    private Long id;

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

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne(mappedBy = "configuration", cascade = CascadeType.ALL)
    private Instrument instrument;

}
