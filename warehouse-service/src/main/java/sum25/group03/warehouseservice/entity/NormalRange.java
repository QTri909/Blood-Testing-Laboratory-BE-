package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.warehouseservice.entity.enums.Gender;
import sum25.group03.warehouseservice.entity.enums.ParamUnit;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "normal_ranges")
public class NormalRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "min_value")
    private Double minValue;

    @Column (name= "max_value")
    private Double maxValue;

    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private ParamUnit unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_parameter_id", nullable = false)
    private TestParameter testParameter;
}
