package sum25.group03.patientservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import sum25.group03.patientservice.feign.dtos.UserFeignResponseWrapper;

@FeignClient(
        name = "iam-service",
        url = "http://localhost:3000"
)
public interface IAMFeignClient {
    @GetMapping("/users")
    public UserFeignResponseWrapper fetchUsersInfo();
}
