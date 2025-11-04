package sum25.group03.warehouseservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "test_parameters")
public class TestParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parameter_name", nullable = false)
    private String parameterName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "abbreviation", nullable = false)
    private String abbreviation;

    @Column(name = "normal_range", nullable = false, length = 512)
    private String normalRange;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_config_id", nullable = false)
    @JsonIgnore
    private GlobalParameterConfiguration globalParameterConfiguration;
}