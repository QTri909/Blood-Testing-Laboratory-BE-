package sum25.group03.patientservice.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.common.response.constants.KafkaConstants;
import sum25.group03.common.response.events.UserCreatedEvent;
import sum25.group03.common.response.events.UserDeletedEvent;
import sum25.group03.common.response.events.UserUpdatedEvent;
import sum25.group03.patientservice.constants.KafkaConstantVars;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.mapper.UserSnapshotMapper;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.UserKafkaConsumerService;
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKafkaConsumerServiceImpl implements UserKafkaConsumerService {

    private final UserSnapshotRepository userRepository;
    private final UserSnapshotMapper userMapper;
    private final UserSnapshotService userSnapshotService;

    @Override
    @KafkaListener(
            topics = {
                    KafkaConstants.USER_CREATED_TOPIC
            },
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void fetchUserFromKafka(@Valid UserCreatedEvent kafkaUserDTO) {

        Set<String> roles = kafkaUserDTO.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("PATIENT");
            kafkaUserDTO.setRoles(roles);
        }
        userSnapshotService.handleCreateUserFromIAM(kafkaUserDTO);
    }

    @Override
    @KafkaListener(
            topics = {
                    KafkaConstants.USER_UPDATED_TOPIC
            },
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void fetchUpdatedUserFromKafka(UserUpdatedEvent kafkaUserDTO) {
        if (kafkaUserDTO.getId() == null)
            return;
        userSnapshotService.handleUpdateUserFromUserFromIAM(kafkaUserDTO);
    }

    @Override
    @KafkaListener(
            topics = {
                    KafkaConstants.USER_DELETED_TOPIC
            },
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void fetchDeletedUserFromKafka(UserDeletedEvent userDeletedEvent) {
        if (userDeletedEvent.getId() == null)
            return;
        userSnapshotService.handleDeleteUserFromIAM(userDeletedEvent);
    }

}
