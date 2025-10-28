package sum25.group03.patientservice.feign.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterUpdate implements Serializable {
    private Long id;
    private List<String> roles;
    private String phone;
    private String email;
    private String fullName;
}