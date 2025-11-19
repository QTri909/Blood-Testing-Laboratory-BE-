package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "global_tests")
public class GlobalTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "test_parameter_id", nullable = false)
    private TestParameter testParameter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_parameter_configuration_id", nullable = false)
    private GlobalParameterConfiguration globalParameterConfiguration;
}
