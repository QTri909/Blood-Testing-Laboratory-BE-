package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import sum25.group03.iamservice.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdentityNumber(String identityNumber);
    boolean existsByEmail(String email);
    boolean existsByIdentityNumber(String identityNumber);

    @EntityGraph(attributePaths = {"userRoles.role.rolePrivileges", "userPrivileges.privilege"})
    Optional<User> findByCognitoUserId(String cognitoUserId);

}
