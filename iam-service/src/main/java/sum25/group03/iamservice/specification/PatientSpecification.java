package sum25.group03.iamservice.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import sum25.group03.iamservice.dto.request.PatientFilterSearchingRequest;
import sum25.group03.iamservice.entity.User;

import java.util.ArrayList;
import java.util.List;

public class PatientSpecification {

    public static Specification<User> buildFromRequest(PatientFilterSearchingRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Ensure user has role "PATIENT"
            // Adjust join path if your User -> UserRole -> Role mapping fields differ
            Join<Object, Object> userRoleJoin = root.join("userRoles", JoinType.LEFT);
            Join<Object, Object> roleJoin = userRoleJoin.join("role", JoinType.LEFT);
            predicates.add(cb.equal(cb.upper(roleJoin.get("roleCode")), "PATIENT"));

            if (req.getFullName() != null && !req.getFullName().isBlank()) {
                // Adjust to match your User entity fields (e.g. firstName/lastName) if needed
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + req.getFullName().toLowerCase() + "%"));
            }

            if (req.getIdentityNumber() != null && !req.getIdentityNumber().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("identityNumber")), "%" + req.getIdentityNumber().toLowerCase() + "%"));
            }

            if (req.getPhoneNumber() != null && !req.getPhoneNumber().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("phoneNumber")), "%" + req.getPhoneNumber().toLowerCase() + "%"));
            }

            if (req.getEmail() != null && !req.getEmail().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + req.getEmail().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}