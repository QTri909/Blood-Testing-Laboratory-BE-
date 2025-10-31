package sum25.group03.iamservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sum25.group03.iamservice.entity.Role;
import sum25.group03.iamservice.entity.User;
import sum25.group03.iamservice.entity.UserRole;
import sum25.group03.iamservice.repository.RoleRepository;
import sum25.group03.iamservice.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        // 1️⃣ Tạo role ADMIN nếu chưa có
        Role adminRole = roleRepository.findByRoleCode("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleCode("ADMIN");
                    newRole.setRoleName("Administrator");
                    return roleRepository.save(newRole);
                });

        // 2️⃣ Tạo user admin mặc định nếu chưa có
        if (!userRepository.existsByEmail("admin@system.com")) {
            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@system.com");
            admin.setIdentityNumber("000000000");
            admin.setPhoneNumber("0900000000");
            admin.setAddress("System Center");

            // Gán role ADMIN
            UserRole userRole = new UserRole();
            userRole.setUser(admin);
            userRole.setRole(adminRole);

            Set<UserRole> roles = new HashSet<>();
            roles.add(userRole);
            admin.setUserRoles(roles);

            userRepository.save(admin);

            System.out.println("Default admin created: admin@system.com");

        }
    }
}
