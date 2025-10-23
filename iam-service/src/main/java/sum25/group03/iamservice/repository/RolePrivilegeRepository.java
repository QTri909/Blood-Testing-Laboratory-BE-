package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;
import sum25.group03.iamservice.entity.Privilege;
import sum25.group03.iamservice.entity.RolePrivilege;

import java.util.List;

public interface RolePrivilegeRepository extends JpaRepositoryImplementation<RolePrivilege,Long> {
    void deleteByRoleId(Long roleId);

    @Query("SELECT rp.privilege FROM RolePrivilege rp WHERE rp.role.id = :roleId")
    List<Privilege> findByRoleId(@Param("roleId") Long roleId);
}
