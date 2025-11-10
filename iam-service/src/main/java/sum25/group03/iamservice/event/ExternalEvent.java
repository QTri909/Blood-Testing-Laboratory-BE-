package sum25.group03.iamservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalEvent {
    private String source;
    private String type;
    private String payload;
}
