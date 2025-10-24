package sum25.group03.instrumentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;

import java.time.LocalDate;

@Entity
@Table(name = "installed_reagent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstalledReagent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Column(name = "current_volume", nullable = false)
    private Double currentVolume;

    @Column(name = "reagent_unit", nullable = false)
    private String unit;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InstalledReagentStatus status;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "lot_reagent_id", nullable = false)
    private Integer lotReagentId;

    @Column(name = "reagent_id", nullable = false)
    private Long reagentId;

    @Column(name = "reagent_name", nullable = false)
    private String reagentName;

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;




}