package sum25.group03.patientservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import sum25.group03.patientservice.dtos.request.FilteredMedicalRecordRequest;
import sum25.group03.patientservice.entities.MedicalRecordEntity;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordSpecification {

    public static Specification<MedicalRecordEntity> buildFromRequest(FilteredMedicalRecordRequest request) {

        return (root, query, cb) -> {
            List<Predicate> predicated = new ArrayList<>();

            if (request.getStatusList() != null && !request.getStatusList().isEmpty()) {
                predicated.add(root.get("status").in(request.getStatusList()));
            }

            return cb.and(predicated.toArray(new Predicate[0]));
        };
    }
}
