package sum25.group03.warehouseservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.Gender;
import sum25.group03.warehouseservice.entity.enums.ParameterStatus;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "price")
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParameterStatus status;

    @Column(name = "unit")
    private  String unit;

    @Column(name = "normal_range", nullable = false, length = 512)
    private String normalRange;
    @Column(name= "min_value")
    private Double minValue;
    @Column (name= "max_value")
    private Double maxValue;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_config_id", nullable = false)
    @JsonIgnore
    private GlobalParameterConfiguration globalParameterConfiguration;
}