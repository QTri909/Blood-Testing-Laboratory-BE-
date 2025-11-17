package sum25.group03.warehouseservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.Gender;
import sum25.group03.warehouseservice.entity.enums.ParameterStatus;

import java.util.List;

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

    @Column(name = "price")
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParameterStatus status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testParameter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GlobalTest> globalTest;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "testParameter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NormalRange> normalRanges;
}