package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.iamservice.entity.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
}
