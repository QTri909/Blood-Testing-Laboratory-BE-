package sum25.group03.testorderservice.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TestOrderPatientInfo implements Serializable {
    private String id; // null if not registered
    private String identityNumber;
    private String fullName;
    private LocalDate dateOfBirth;
    private Integer age;
    private String gender;
    private String address;
    private String phoneNumber;
    private String email;

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
