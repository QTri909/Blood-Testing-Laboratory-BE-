package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.iamservice.entity.Privilege;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    @Query("SELECT p FROM Privilege p JOIN RolePrivilege rp ON rp.privilege = p WHERE rp.role.id = :roleId")
    List<Privilege> findByRoleId(@Param("roleId") Long roleId);

    boolean existsByPrivilegeCode(String privilegeCode);
}
