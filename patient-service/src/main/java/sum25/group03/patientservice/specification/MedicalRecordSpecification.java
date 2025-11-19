package sum25.group03.patientservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import sum25.group03.patientservice.dtos.request.FilteredMedicalRecordRequest;
import sum25.group03.patientservice.entities.MedicalRecordEntity;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

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

    public static Specification<MedicalRecordEntity> buildAssignableFromRequest(FilteredMedicalRecordRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicated = new ArrayList<>();

            // assignable = [belongs to a specific patient && status = (PUBLISHED, EMPTY)] or [not assigned to any patient && status = (EMPTY)]
            Long patientId = request.getPatientId();

            Predicate belongsToPatient = cb.equal(root.get("patientId"), patientId);
            Predicate statusIsPublishedOrEmpty = root.get("status").in(
                    List.of(
                            MedicalRecordStatus.PUBLISHED,
                            MedicalRecordStatus.EMPTY
                    )
            );
            Predicate notAssignedToAnyPatient = cb.isNull(root.get("patientId"));
            Predicate statusIsEmpty = cb.equal(root.get("status"), MedicalRecordStatus.EMPTY);
            predicated.add(
                    cb.or(
                            cb.and(belongsToPatient, statusIsPublishedOrEmpty),
                            cb.and(notAssignedToAnyPatient, statusIsEmpty)
                    )
            );
            return cb.and(predicated.toArray(new Predicate[0]));
        };
    }
}
