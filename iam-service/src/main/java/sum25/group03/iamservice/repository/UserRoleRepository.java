package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.iamservice.entity.UserRole;


import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    void deleteByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);

}
