package sum25.group03.instrumentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstrumentStatus;

import java.util.List;


@Entity
@Table(name = "instruments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_name")
    private String instrumentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstrumentStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "configuration_id")
    private Configuration configuration;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstalledReagent> installedReagents;
}