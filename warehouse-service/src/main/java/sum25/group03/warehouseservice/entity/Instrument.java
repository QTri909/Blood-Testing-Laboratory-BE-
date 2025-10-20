package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @Column(name = "id")
    private Long instrumentId;

    @Column(name = "instrument_name", nullable = false)
    private String instrumentName;

    @Column(name = "instrument_code", nullable = false)
    private String instrumentCode;

    @Column(name = "instrument_type", nullable = false)
    private String instrumentType;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "firmware_version", nullable = false)
    private String firmwareVersion;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InstrumentStatus status;

    @Column(name = "notes", nullable = true, columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "deactivated_at", nullable = false)
    private LocalDate deactivatedAt;

    @Column(name = "deactivated_by", nullable = false)
    private int deactivatedBy;

    @Column(name = "auto_delete_scheduled_at", nullable = true)
    private LocalDate autoDeleteScheduledAt;

    @Column(name = "installation_date", nullable = false)
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
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "created_by", nullable = false)
    private int createdBy;

    @Column(name = "updated_by", nullable = false)
    private int updatedBy;

    @OneToOne(fetch = FetchType.LAZY, cascade =  {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "configuration_id", nullable = true)
    private Configurations configuration;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistoryUsage> reagentHistoryUsages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_test_results_id", nullable = true)
    private RawTestResults rawTestResults;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstalledReagent> installedReagents;
}
