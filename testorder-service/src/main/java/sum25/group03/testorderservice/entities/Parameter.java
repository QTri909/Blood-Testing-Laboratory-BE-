package sum25.group03.testorderservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.testorderservice.enums.ParameterGender;
import sum25.group03.testorderservice.enums.ParameterStatus;
import sum25.group03.testorderservice.enums.ParameterUnit;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "parameters")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Parameter {

    @Id
    private Long id;

    @Column(name = "param_code", nullable = false, unique = true)
    private String paramCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "description")
    private String description;

    private Double min;
    private Double max;

    @Enumerated(EnumType.STRING)
    private ParameterUnit unit;

    @Enumerated(EnumType.STRING)
    private ParameterStatus status;

    @Enumerated(EnumType.STRING)
    private ParameterGender gender;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default // Ensures that the field is initialized during the build process
    private LocalDate createdAt = null;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDate updatedAt = null;

    private Long createdBy;
    private Long updatedBy;
    private Long price;

    @OneToMany(mappedBy = "parameter", cascade = CascadeType.ALL)
    private Set<TestResult> testResults;

    @PrePersist
    public void prePersist() {
        if (this.status == null)
            this.status = ParameterStatus.ACTIVE;
        if (this.gender == null)
            this.gender = ParameterGender.BOTH;
    }
}
