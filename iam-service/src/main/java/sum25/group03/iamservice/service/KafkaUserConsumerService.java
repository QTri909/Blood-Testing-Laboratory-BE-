package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.entity.PendingUser;
import sum25.group03.iamservice.event.UserCreatedEvent;

import sum25.group03.iamservice.repository.PendingUserRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUserConsumerService {

    private final PendingUserRepository pendingUserRepository;

    @KafkaListener(
            topics = "test-order-user-created",
            groupId = "iam-service-group",
            containerFactory = "userCreatedEventKafkaListenerContainerFactory"
    )


    public void handleUserCreatedFromTestOrder(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent from TestOrder-service: {}", event);
        try {
            PendingUser pending = PendingUser.builder()
                    .fullName(event.getFullName())
                    .email(event.getEmail())
                    .phoneNumber(event.getPhoneNumber())
                    .gender(event.getGender())
                    .dateOfBirth(event.getDateOfBirth())
                    .identityNumber(event.getIdentityNumber())
                    .address(event.getAddress())
                    .roleCodes(event.getRoles())
                    .approved(false)
                    .receivedAt(LocalDateTime.now())
                    .build();
            pendingUserRepository.save(pending);
            log.info("Saved pending user: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to save pending user: {}", e.getMessage(), e);
        }
    }
}
