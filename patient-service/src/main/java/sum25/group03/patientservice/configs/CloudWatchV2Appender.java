package sum25.group03.patientservice.configs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Collections;

@Setter
public class CloudWatchV2Appender extends AppenderBase<ILoggingEvent> {

    private CloudWatchLogsClient client;
    private String logGroupName;
    private String logStreamName;

    private String sequenceToken; // for CloudWatch

    @Override
    public void start() {
        client = CloudWatchLogsClient.create(); // picks up AWS credentials

        if (logGroupName == null || logGroupName.isEmpty()) {
            return;
        }
        if (logStreamName == null || logStreamName.isEmpty()) {
            logStreamName = getHostName() + "-" + getStreamTimeStampFormatted() + "-" + getPID();
        }

        // ensure log group and stream exist, if not, automatically create them
        ensureLogGroupExists();
        ensureLogStreamExists();

        // start the appender
        super.start();
    }


    @Override
    protected void append(ILoggingEvent eventOject) {

        if (client == null || !isStarted()) return;

        try {
            // build log event
            InputLogEvent inputLogEvent = InputLogEvent.builder()
                    .message(eventOject.getFormattedMessage())
                    .timestamp(Instant.now().toEpochMilli())
                    .build();

            // send log event to CloudWatch
            PutLogEventsRequest.Builder reqBuilder = PutLogEventsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamName(logStreamName)
                    .logEvents(Collections.singletonList(inputLogEvent));

            if (sequenceToken != null) {
                reqBuilder.sequenceToken(sequenceToken);
            }

            PutLogEventsResponse response = client.putLogEvents(reqBuilder.build());
            sequenceToken = response.nextSequenceToken();

        } catch (Exception e) {
            addError("Failed to push log to CloudWatch", e);
        }

    }

    // ---------------- darKernel Utilities ----------------//
    private void ensureLogGroupExists() {
        try {
            client.createLogGroup(CreateLogGroupRequest.builder()
                    .logGroupName(logGroupName)
                    .build());
        } catch (ResourceAlreadyExistsException ignored) {
            // log group already exists
        }
    }

    private void ensureLogStreamExists() {
        try {
            client.createLogStream(CreateLogStreamRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamName(logStreamName)
                    .build());
        } catch (ResourceAlreadyExistsException ignored) {
            // log stream already exists
        }
    }

    private String getPID() {
        // JVM name is like: "12345@hostname"
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return jvmName.split("@")[0];
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }

    private String getStreamTimeStampFormatted() {
        // yyyy-MM-dd-HH-mm-ss is safe for CloudWatch
        String format = "yyyy-MM-dd--HH-mm-ss";
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(format));
    }


}
