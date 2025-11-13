package sum25.group03.iamservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.dto.response.PrivilegeResponse;
import sum25.group03.iamservice.entity.Privilege;
import sum25.group03.iamservice.repository.PrivilegeRepository;
import sum25.group03.iamservice.service.Interface.PrivilegeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeServiceImpl implements PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    @Override
    public Page<PrivilegeResponse> getAllPrivileges(Pageable pageable) {

        Page<Privilege> privileges = privilegeRepository.findAll(pageable);

        return privileges.map(p ->
                PrivilegeResponse.builder()
                        .id(p.getId())
                        .privilegeName(p.getPrivilegeName())
                        .privilegeCode(p.getPrivilegeCode())
                        .privilegeDescription(p.getPrivilegeDescription())
                        .build()
        );
    }
}
