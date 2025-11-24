package sum25.group03.iamservice.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.iamservice.entity.User;

import java.util.List;

import org.springframework.stereotype.Repository;
import sum25.group03.iamservice.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {


    boolean existsByEmail(String email);
    boolean existsByIdentityNumber(String identityNumber);
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleCode = :roleCode")
    Page<User> findByRoleCode(String roleCode, Pageable pageable);

    @EntityGraph(attributePaths = {"userRoles.role.rolePrivileges", "userPrivileges.privilege"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"userRoles.role.rolePrivileges", "userPrivileges.privilege"})
    Optional<User> findByEmailOrIdentityNumber(String email, String identityNumber);


    Optional<User> findByIdentityNumber(String identityNumber);


    @EntityGraph(attributePaths = {"userRoles.role.rolePrivileges", "userPrivileges.privilege"})
    Optional<User> findByCognitoUserId(String cognitoUserId);

    @Modifying
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void deactivateById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteUserById(@Param("id") Long id);

    @Query("""
    SELECT u FROM User u
    WHERE NOT EXISTS (
        SELECT 1 
        FROM UserRole ur
        JOIN ur.role r
        WHERE ur.user = u 
          AND r.roleCode = :roleCode
    )
""")
    Page<User> findUsersNotHavingRole(@Param("roleCode") String roleCode, Pageable pageable);

}
