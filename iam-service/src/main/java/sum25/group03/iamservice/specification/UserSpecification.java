package sum25.group03.iamservice.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import sum25.group03.iamservice.dto.request.UserFilterSearchingRequest;
import sum25.group03.iamservice.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    private static final String ROLE_PATIENT = "PATIENT";

    public static Specification<User> buildFromRequest(UserFilterSearchingRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Normalize input
            String fullName = safe(req.getFullName());
            String identityNumber = safe(req.getIdentityNumber());
            String phoneNumber = safe(req.getPhoneNumber());
            String email = safe(req.getEmail());

            // ==========================
            // ROLE FILTERING
            // ==========================
            if (req.getRoles() != null && !req.getRoles().isEmpty()) {
                // Join only if necessary
                Join<Object, Object> userRoleJoin = root.join("userRoles", JoinType.LEFT);
                Join<Object, Object> roleJoin = userRoleJoin.join("role", JoinType.LEFT);

                // Convert requested roles to uppercase
                List<String> rolesUpper =
                        req.getRoles().stream().map(String::toUpperCase).toList();

                // Filter using IN (better than equals)
                predicates.add(roleJoin.get("roleCode").in(rolesUpper));

                // Avoid duplicated rows if multiple roles
                query.distinct(true);
            }

            // ==========================
            // TEXT FIELDS
            // ==========================
            if (!fullName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), contains(fullName)));
            }

            if (!identityNumber.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("identityNumber")), contains(identityNumber)));
            }

            if (!phoneNumber.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("phoneNumber")), contains(phoneNumber)));
            }

            if (!email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), contains(email)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Helpers
    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String contains(String value) {
        return "%" + value.toLowerCase() + "%";
    }

}