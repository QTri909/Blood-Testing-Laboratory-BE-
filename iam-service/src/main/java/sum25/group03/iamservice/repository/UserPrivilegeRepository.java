package sum25.group03.iamservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import sum25.group03.iamservice.entity.UserPrivilege;

import java.util.List;

@Repository
public interface UserPrivilegeRepository extends JpaRepository<UserPrivilege,Integer> {
    void deleteByUserId(Long userId);
    void deleteByUserIdIn(List<Long> userIds);

}
