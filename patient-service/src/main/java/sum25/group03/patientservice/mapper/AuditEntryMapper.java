package sum25.group03.patientservice.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import sum25.group03.patientservice.documents.AuditEntryDocument;
import sum25.group03.patientservice.dtos.response.AuditEntryResponseDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditEntryMapper {

    // common mappings
    AuditEntryResponseDTO toDto(AuditEntryDocument document);
    List<AuditEntryResponseDTO> toDtoList(List<AuditEntryDocument> documents);
}
