package sum25.group03.instrumentservice.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import sum25.group03.instrumentservice.audit.model.AuditLog;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudWatchAuditLogger {
    private final CloudWatchLogsClient cloudWatchLogsClient;
    private final ObjectMapper objectMapper;

    @Value("${audit.cloudwatch.log-group:Instrument-service}")
    private String logGroup;

    @Value("${audit.cloudwatch.enabled:true}")
    private boolean enabled;

    private static final DateTimeFormatter STREAM_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd/HH").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    public void log(AuditLog auditLog) {
        if (!enabled) {
            log.debug("CloudWatch audit logging is disabled");
            return;
        }

        try {
            String logStream = buildLogStreamName(auditLog);
            ensureLogStreamExists(logStream);

            String logMessage = objectMapper.writeValueAsString(auditLog);
            putLogEvent(logStream, logMessage, auditLog.getTimestamp());

            log.debug("Audit log sent to CloudWatch: {}", logStream);
        } catch (Exception e) {
            log.error("Failed to send audit log to CloudWatch", e);
        }
    }

    public void logSystemEvent(AuditLog auditLog) {
        if (!enabled) {
            log.debug("CloudWatch audit logging is disabled");
            return;
        }

        try {
            String logStream = buildSystemLogStreamName(auditLog);
            ensureLogStreamExists(logStream);

            String logMessage = objectMapper.writeValueAsString(auditLog);
            putLogEvent(logStream, logMessage, auditLog.getTimestamp());

            log.debug("System audit log sent to CloudWatch: {}", logStream);
        } catch (Exception e) {
            log.error("Failed to send system audit log to CloudWatch", e);
        }
    }

    private String buildLogStreamName(AuditLog auditLog) {
        String actionType = auditLog.getAction().getType().toLowerCase();
        String actorType = auditLog.getActor().getType().toLowerCase();
        String timestamp = STREAM_DATE_FORMATTER.format(auditLog.getTimestamp());

        return String.format("%s/%s/%s", actorType, actionType, timestamp);
    }

    private String buildSystemLogStreamName(AuditLog auditLog) {
        String timestamp = STREAM_DATE_FORMATTER.format(auditLog.getTimestamp());
        return String.format("system/write/%s", timestamp);
    }

    private void ensureLogStreamExists(String logStreamName) {
        try {
            DescribeLogStreamsRequest describeRequest = DescribeLogStreamsRequest.builder()
                    .logGroupName(logGroup)
                    .logStreamNamePrefix(logStreamName)
                    .build();

            DescribeLogStreamsResponse response = cloudWatchLogsClient.describeLogStreams(describeRequest);

            if (response.logStreams().isEmpty()) {
                CreateLogStreamRequest createRequest = CreateLogStreamRequest.builder()
                        .logGroupName(logGroup)
                        .logStreamName(logStreamName)
                        .build();
                cloudWatchLogsClient.createLogStream(createRequest);
                log.debug("Created new log stream: {}", logStreamName);
            }
        } catch (ResourceAlreadyExistsException e) {
            log.debug("Log stream already exists: {}", logStreamName);
        } catch (Exception e) {
            log.error("Error ensuring log stream exists: {}", logStreamName, e);
        }
    }

    private void putLogEvent(String logStreamName, String message, Instant timestamp) {
        try {
            InputLogEvent logEvent = InputLogEvent.builder()
                    .message(message)
                    .timestamp(timestamp.toEpochMilli())
                    .build();

            PutLogEventsRequest putRequest = PutLogEventsRequest.builder()
                    .logGroupName(logGroup)
                    .logStreamName(logStreamName)
                    .logEvents(logEvent)
                    .build();

            cloudWatchLogsClient.putLogEvents(putRequest);
        } catch (Exception e) {
            log.error("Error putting log event to CloudWatch", e);
        }
    }
}
