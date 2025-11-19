package sum25.group03.patientservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.patientservice.enums.ActionTypeFeatures;

import java.time.LocalDateTime;

@Data
@Builder
public class ActionLogDTO {
    private Long actorId;
    private ActionTypeFeatures action;
    private Long targetId;
    private LocalDateTime actionTime;

    @Override
    public String toString() {
        return "{" +
                "\"actorId\": " + actorId +
                ", \"action\": \"" + action + '\"' +
                ", \"targetId\": " + targetId +
                ", \"actionTime\": \"" + actionTime + '\"' +
                '}';
    }
}
