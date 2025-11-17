package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.warehouseservice.entity.enums.Gender;

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

//    @Column(name = "normal_range", nullable = false, length = 512)
//    private String normalRange;
    @Column(name= "min_value")
    private Double minValue;

    @Column (name= "max_value")
    private Double maxValue;

    @Column(name = "unit")
    private  String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_parameter_id", nullable = false)
    private TestParameter testParameter;
}
