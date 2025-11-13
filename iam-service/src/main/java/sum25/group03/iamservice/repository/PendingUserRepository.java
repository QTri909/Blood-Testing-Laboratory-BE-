package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.group03.iamservice.entity.PendingUser;

import java.beans.JavaBean;
import java.util.List;

public interface PendingUserRepository extends JpaRepository<PendingUser,Long> {
    List<PendingUser> findByApprovedFalse();
}
