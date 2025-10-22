package sum25.group03.patientservice.mapper;

import org.mapstruct.*;
import sum25.group03.patientservice.dtos.request.ClinicalNoteRequest;
import sum25.group03.patientservice.dtos.response.ClinicalNoteResponse;
import sum25.group03.patientservice.entities.ClinicalNoteEntity;

@Mapper(componentModel = "spring")
public interface ClinicalNoteMapper {

    @Mapping(target = "noteId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "medicalRecord", ignore = true)
    @Mapping(target = "notedByUser", ignore = true)
    ClinicalNoteEntity toEntity(ClinicalNoteRequest request);

    ClinicalNoteResponse toResponse(ClinicalNoteEntity entity);
}
