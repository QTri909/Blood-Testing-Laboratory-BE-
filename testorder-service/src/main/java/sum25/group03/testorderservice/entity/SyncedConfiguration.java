package sum25.group03.testorderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.testorderservice.enums.SyncedConfigurationStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "synced_configurations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncedConfiguration {

    @Id
    private Long id;

    private String configKey;
    private Double minValue;
    private Double maxValue;
    private String description;
    private String unit;

    @Enumerated(EnumType.STRING)
    private SyncedConfigurationStatus status;

    @UpdateTimestamp
    private LocalDateTime syncedAt;

    // 1 synced configuration can belong to many parameters
    @OneToMany(
            mappedBy = "syncedConfiguration",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<TestResult> testResults;

    @Override
    public String toString() {
        return "SyncedConfiguration{" +
                "id=" + id +
                ", configKey='" + configKey + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", status=" + status +
                ", syncedAt=" + syncedAt +
                '}';
    }
}
