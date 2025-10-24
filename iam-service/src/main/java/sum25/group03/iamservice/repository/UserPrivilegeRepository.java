package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.iamservice.entity.UserPrivilege;

public interface UserPrivilegeRepository extends JpaRepository<UserPrivilege, Long> {
}
