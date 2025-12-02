package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.iamservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.iamservice.entity.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleCode(String roleCode);


    @Query("SELECT r FROM Role r JOIN UserRole ur ON ur.role = r WHERE ur.user.id = :userId")
    List<Role> findByUserId(@Param("userId") Long userId);


    boolean existsByRoleName(String roleName);
    boolean existsByRoleCode(String roleCode);

    @Query("SELECT r FROM Role r WHERE r.userRoles IS EMPTY")
    List<Role> findRolesWithoutUsers();

    boolean existsByRoleCodeAndIdNot(String roleCode, Long id);



}
