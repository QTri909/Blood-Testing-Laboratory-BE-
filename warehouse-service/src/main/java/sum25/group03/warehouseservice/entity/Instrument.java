package sum25.group03.warehouseservice.entity; 

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "instruments")
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instrument_id")
    private Long instrumentId;

    @Column(name = "instrument_name", nullable = false)
    private String instrumentName;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;


    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InstrumentStatus status;

    @Column(name = "notes", nullable = true, columnDefinition = "TEXT")
    private String notes;


    @Column(name = "deactivated_at")
    private LocalDate deactivatedAt;

    @Column(name = "deactivated_by")
    private Integer deactivatedBy;

    @Column(name = "auto_delete_scheduled_at")
    private LocalDate autoDeleteScheduledAt;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "last_calibration_date", nullable = true)
    private LocalDate lastCalibrationDate;

    @Column(name = "next_calibration_date", nullable = true)
    private LocalDate nextCalibrationDate;

    @Column(name = "last_maintenance_date", nullable = true)
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_maintenance_date", nullable = true)
    private LocalDate nextMaintenanceDate;


    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;


    @Column(name = "created_by", nullable = true)
    private int createdBy;

    @Column(name = "updated_by", nullable = true)
    private int updatedBy;


    @OneToOne(fetch = FetchType.LAZY, cascade =  {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "specific_configuration_id", nullable = true)
    private SpecificConfiguration specificConfiguration;


    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistoryUsage> reagentHistoryUsages;


}



