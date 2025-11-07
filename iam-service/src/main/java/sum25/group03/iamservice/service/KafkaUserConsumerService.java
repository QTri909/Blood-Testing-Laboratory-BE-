package sum25.group03.iamservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.dto.request.UserCreateRequest;
import sum25.group03.iamservice.event.UserCreatedEvent;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUserConsumerService {

    private final UserService userService;

    @KafkaListener(
            topics = "test-order-user-created", // topic do TestOrder-service gửi
            groupId = "iam-service-group",
            containerFactory = "userCreatedEventKafkaListenerContainerFactory"
    )
    public void handleUserCreatedFromTestOrder(UserCreatedEvent event) {
        log.info("📩 Received UserCreatedEvent from TestOrder-service: {}", event);

        try {
            // Chuyển event sang request phù hợp với createUser()
            UserCreateRequest request = new UserCreateRequest();
            request.setFullName(event.getFullName());
            request.setEmail(event.getEmail());
            request.setPassword("Temp@123"); // 👈 đặt mật khẩu tạm
            request.setPhoneNumber(event.getPhoneNumber());
            request.setGender(event.getGender() != null ? event.getGender().toUpperCase() : "OTHER");
            request.setDateOfBirth(event.getDateOfBirth() != null ? event.getDateOfBirth() : LocalDate.now());
            request.setIdentityNumber(event.getIdentityNumber());
            request.setAddress(event.getAddress());
            request.setRoleCodes(
                    (event.getRoles() != null && !event.getRoles().isEmpty())
                            ? event.getRoles()
                            : Set.of("USER") // 👈 mặc định nếu không có
            );

            userService.createUser(request);
            log.info("Created user from Kafka event: {}", event.getEmail());

        } catch (Exception e) {
            log.error("Failed to create user from TestOrder event: {}", e.getMessage(), e);
        }
    }
}
