package sum25.group03.common.response.constants;

public class KafkaConstants {

    // payment-service topics
    public static final String PAYMENT_RESULT_TOPIC = "payment_result_topic";

    // iam-service topics
    public static final String USER_CREATED_TOPIC = "iam.user.created";
    public static final String USER_UPDATED_TOPIC = "iam.user.updated";
    public static final String USER_DELETED_TOPIC = "iam.user.deleted";

    // monitoring topics
    public static final String MONITORING_LOG_TOPIC = "monitoring-log";
}
