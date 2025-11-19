package sum25.group03.common.response.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserCreatedEvent implements Serializable {
    private String id; // null if not registered
    private String email;
    private String fullName;
    private String phoneNumber;
    private String identityNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private Set<String> roles;
    private Set<String> privileges;

    @Override
    public String toString() {
        return "{" +
                "patientId='" + id + '\'' +
                ", identificationNumber='" + identityNumber + '\'' +
                ", name='" + fullName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
