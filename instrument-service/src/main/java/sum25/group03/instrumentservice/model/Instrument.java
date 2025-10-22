package sum25.group03.instrumentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.InstrumentStatus;


@Entity
@Table(name = "instruments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "instrument_code")
    private String instrumentCode;

    @Column(name = "instrument_name")
    private String instrumentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstrumentStatus status;

    @ManyToOne
    @JoinColumn(name = "configuration_id")
    private Configuration configuration;
}