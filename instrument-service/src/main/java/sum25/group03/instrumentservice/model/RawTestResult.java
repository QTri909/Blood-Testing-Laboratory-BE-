package sum25.group03.instrumentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_test_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawTestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResultID")
    private Long resultId;

    @Column(name = "TestOrderID")
    private Long testOrderId;

    @ManyToOne
    @JoinColumn(name = "InstrumentID")
    private Instrument instrument;

    @Column(name = "RawData", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "HL7Message", columnDefinition = "TEXT")
    private String hl7Message;

    @Column(name = "IsSentToMonitoring")
    private Boolean isSentToMonitoring;

    @Column(name = "IsSynced")
    private Boolean isSynced;

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
