package sum25.group03.patientservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.patientservice.dtos.response.ActionLogDTO;
import sum25.group03.patientservice.enums.ActionTypeFeatures;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ActionLogService {

    public void logAction(Long actorId, ActionTypeFeatures action, Long targetId) {

        ActionLogDTO actionLogDTO = ActionLogDTO.builder()
                .actorId(actorId)
                .action(action)
                .targetId(targetId)
                .actionTime(LocalDateTime.now())
                .build();

        log.info(actionLogDTO.toString());
    }
}
