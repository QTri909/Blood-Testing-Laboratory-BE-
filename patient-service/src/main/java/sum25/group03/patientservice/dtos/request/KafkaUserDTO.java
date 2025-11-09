package sum25.group03.patientservice.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.annotations.NotNull;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KafkaUserDTO {
    @NotNull
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private List<Integer> dateOfBirth;
    private String identityNumber;
    private String address;
    private List<String> roles;
    private List<String> privileges;
}
