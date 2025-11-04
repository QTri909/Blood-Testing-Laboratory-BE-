package sum25.group03.instrumentservice.audit.provider;

import org.springframework.stereotype.Component;
import sum25.group03.instrumentservice.audit.model.ActorContext;

import java.util.UUID;

@Component
public class MockActorProvider implements ActorProvider {
    private static final String[] MOCK_USERS = {
            "admin@example.com",
            "technician@example.com",
            "operator@example.com",
            "supervisor@example.com"
    };

    @Override
    public ActorContext getCurrentActor() {
        String username = MOCK_USERS[(int) (Math.random() * MOCK_USERS.length)];
        String userId = "u-" + UUID.randomUUID();
        return ActorContext.mockUser(userId, username);
    }
}
