package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpiryReagent implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long reagentId;
    private String reagentName;
    private String lotNumber;
    private Long lotReagentId;
    private String eventTimestamp;
}
