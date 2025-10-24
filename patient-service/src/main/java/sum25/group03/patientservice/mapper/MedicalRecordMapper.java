package sum25.group03.patientservice.mapper;

import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sum25.group03.patientservice.documents.MedicalRecordDocument;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.entities.MedicalRecordEntity;

import java.util.List;



@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {

    public MedicalRecordResponse toMedicalRecordResponse(MedicalRecordEntity entity);
    public List<MedicalRecordResponse> toMedicalRecordResponseList(List<MedicalRecordEntity> entities);

    @Mapping(target = "recordId", ignore = true)
    @Mapping(target = "recordCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "assignedUserDetails", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "updatedByUser", ignore = true)
    @Mapping(target = "clinicalNotes", ignore = true)
    MedicalRecordEntity toEntity(MedicalRecordRequest request);

    public MedicalRecordDocument toMedicalRecordDocument(MedicalRecordEntity entity);
}