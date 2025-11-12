package sum25.group03.testorderservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.dtos.response.ActionLogDTO;
import sum25.group03.testorderservice.enums.ActionTypeFeatures;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ActionLogService {

    public void logAction(Long actorId, ActionTypeFeatures action, Long targetId) {
        ActionLogDTO actionLog = ActionLogDTO.builder()
                .actorId(actorId)
                .action(action)
                .targetId(targetId)
                .actionTime(LocalDateTime.now())
                .build();

        // In ra console (và sẽ hiển thị trong logs)
        log.info(actionLog.toString());
    }
}
