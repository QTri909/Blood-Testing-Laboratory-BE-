package sum25.group03.patientservice.mapper;

import org.mapstruct.Mapper;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.entities.MedicalRecordEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {

    public MedicalRecordResponse toMedicalRecordResponse(MedicalRecordEntity entity);
    public List<MedicalRecordResponse> toMedicalRecordResponseList(List<MedicalRecordEntity> entities);
}
