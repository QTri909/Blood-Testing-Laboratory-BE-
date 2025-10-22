package sum25.group03.patientservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import sum25.group03.patientservice.documents.MedicalRecordDocument;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.entities.MedicalRecordEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {

    public MedicalRecordResponse toMedicalRecordResponse(MedicalRecordEntity entity);
    public List<MedicalRecordResponse> toMedicalRecordResponseList(List<MedicalRecordEntity> entities);

    public MedicalRecordDocument toMedicalRecordDocument(MedicalRecordEntity entity);
}
