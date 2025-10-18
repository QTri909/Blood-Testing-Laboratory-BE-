package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "configurations")
public class Configurations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuration_id")
    private Long configurationId;

    @Column(name = "configuration_key", nullable = false)
    private String configurationKey;

    @Column(name = "configuration_value", nullable = false, columnDefinition = "TEXT")
    private String configurationValue;

    @Column(name = "configuration_category", nullable = false)
    private String configurationCategory;

    @Column(name = "instrument_type", nullable = false)
    private String instrumentType;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "created_by", nullable = false)
    private int createdBy;

    @Column(name = "updated_by", nullable = false)
    private int updatedBy;

    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Instrument> instruments;
}
