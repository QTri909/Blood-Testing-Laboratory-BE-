package sum25.group03.testorderservice.dtos.response;

import lombok.Builder;
import lombok.Data;
import sum25.group03.testorderservice.enums.ActionTypeFeatures;

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
