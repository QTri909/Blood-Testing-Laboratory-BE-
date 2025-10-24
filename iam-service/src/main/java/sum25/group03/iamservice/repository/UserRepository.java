package sum25.group03.iamservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.iamservice.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByIdentityNumber(String identityNumber);
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleCode = :roleCode")
    Page<User> findByRoleCode(String roleCode, Pageable pageable);
}
