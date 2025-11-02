package sum25.group03.testorderservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.enums.ActionCommentsLog;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CommentLogServiceImpl {

    public void logAction(ActionCommentsLog action, Long userId, Long commentId, String oldValue, String newValue) {
        log.info("""
                [AUDIT LOG]
                Action: {}
                User ID: {}
                Comment ID: {}
                Old Value: {}
                New Value: {}
                Timestamp: {}
                """,
                action, userId, commentId,
                oldValue != null ? oldValue : "N/A",
                newValue != null ? newValue : "N/A",
                LocalDateTime.now()
        );
    }
}
