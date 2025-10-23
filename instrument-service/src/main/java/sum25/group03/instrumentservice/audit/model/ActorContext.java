package sum25.group03.instrumentservice.audit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorContext {
    private String type;
    private String id;
    private String username;
    private String principal;

    public static ActorContext mockUser(String userId, String username) {
        return ActorContext.builder()
                .type("USER")
                .id(userId)
                .username(username)
                .principal("arn:aws:iam::123456789012:user/" + username)
                .build();
    }

    public static ActorContext mockSystem() {
        return ActorContext.builder()
                .type("SYSTEM")
                .id("system-service")
                .username("system")
                .principal("arn:aws:service:instrument-service")
                .build();
    }
}
