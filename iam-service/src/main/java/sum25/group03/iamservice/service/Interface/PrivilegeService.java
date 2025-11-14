package sum25.group03.iamservice.service.Interface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.iamservice.dto.response.PrivilegeResponse;

import java.util.List;

public interface PrivilegeService {
    Page<PrivilegeResponse> getAllPrivileges(Pageable pageable);
}
