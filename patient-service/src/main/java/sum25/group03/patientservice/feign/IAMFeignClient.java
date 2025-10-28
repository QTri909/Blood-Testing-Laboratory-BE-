package sum25.group03.patientservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sum25.group03.patientservice.feign.dtos.FeignPatientResponseWrapper;
import sum25.group03.patientservice.feign.dtos.FeignUserResponseWrapper;

@FeignClient(
        name = "iam-service",
        url = "http://localhost:3000"
)
public interface IAMFeignClient {
    @GetMapping("/users")
    public FeignUserResponseWrapper fetchUsersInfo(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    );

    @GetMapping("/patients")
    public FeignPatientResponseWrapper fetchPatientsInfo(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    );
}
