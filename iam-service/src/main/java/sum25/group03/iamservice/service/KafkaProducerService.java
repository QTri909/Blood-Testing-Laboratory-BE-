package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.common.response.events.UserCreatedEvent;
import sum25.group03.iamservice.event.*;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, UserCreatedEvent> userCreatedKafkaTemplate;
    private final KafkaTemplate<String, UserUpdatedEvent> userUpdatedKafkaTemplate;
    private final KafkaTemplate<String, UserDeletedEvent> userDeletedKafkaTemplate;
    private final KafkaTemplate<String, RoleCreatedEvent> roleCreatedKafkaTemplate;
    private final KafkaTemplate<String, RoleUpdatedEvent> roleUpdatedKafkaTemplate;
    private final KafkaTemplate<String, RoleDeletedEvent> roleDeletedKafkaTemplate;
    private final KafkaTemplate<String, PasswordChangedEvent> passwordChangedKafkaTemplate;

    public void sendUserCreated(UserCreatedEvent event) {
        userCreatedKafkaTemplate.send("iam.user.created", event);
    }

    public void sendUserUpdated(UserUpdatedEvent event) {
        userUpdatedKafkaTemplate.send("iam.user.updated", event);
    }

    public void sendUserDeleted(UserDeletedEvent event) {
        userDeletedKafkaTemplate.send("iam.user.deleted", event);
    }

    public void sendRoleCreated(RoleCreatedEvent event) {
        roleCreatedKafkaTemplate.send("iam.role.created", event);
    }

    public void sendRoleUpdated(RoleUpdatedEvent event) {
        roleUpdatedKafkaTemplate.send("iam.role.updated", event);
    }

    public void sendRoleDeleted(RoleDeletedEvent event) {
        roleDeletedKafkaTemplate.send("iam.role.deleted", event);
    }

    public void sendPasswordChanged(PasswordChangedEvent event) {
        passwordChangedKafkaTemplate.send("iam.password.changed", event);
    }


}
