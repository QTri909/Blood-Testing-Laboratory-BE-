package sum25.group03.testorderservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.enums.ActionTypeFeatures;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ActionLogService {

    public void logAction(Long viewerId, ActionTypeFeatures action, Long targetId) {
        LocalDateTime timestamp = LocalDateTime.now();

        // In ra console (và sẽ hiển thị trong logs)
        if (targetId == null) {
            log.info("User {} - {} - {}", viewerId, action, timestamp);
            return;
        }
        log.info("User {} - {} - (targetId={}) - {}", viewerId, action, targetId, timestamp);
    }
}
