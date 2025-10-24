package sum25.group03.testorderservice.specification;

import org.springframework.data.jpa.domain.Specification;
import sum25.group03.testorderservice.entity.TestOrder;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestOrderSpecification {

    public static Specification<TestOrder> hasStatus(TestOrderStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<TestOrder> hasCreatedBy(Long createdBy) {
        return (root, query, cb) -> {
            if (createdBy == null) return cb.conjunction();
            return cb.equal(root.get("createdBy"), createdBy);
        };
    }

    public static Specification<TestOrder> hasRunBy(Long runBy) {
        return (root, query, cb) -> {
            if (runBy == null) return cb.conjunction();
            return cb.equal(root.get("runBy"), runBy);
        };
    }

    public static Specification<TestOrder> createdBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();

            LocalDateTime start = from != null ? from.atStartOfDay() : LocalDate.MIN.atStartOfDay();
            LocalDateTime end = to != null ? to.atTime(23, 59, 59) : LocalDate.MAX.atTime(23, 59, 59);

            return cb.between(root.get("createdAt"), start, end);
        };
    }
}
