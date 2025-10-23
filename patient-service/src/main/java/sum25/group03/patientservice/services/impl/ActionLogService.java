package sum25.group03.patientservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.patientservice.enums.ActionTypeFetures;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ActionLogService {

    public void logAction(Long viewerId, ActionTypeFetures action, Long targetId) {
        LocalDateTime timestamp = LocalDateTime.now();

        // In ra console (và sẽ hiển thị trong logs)
        log.info(" User {} viewed {} (targetId={}) at {}", viewerId, action, targetId, timestamp);
    }
}
