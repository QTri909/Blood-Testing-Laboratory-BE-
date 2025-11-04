package sum25.group03.patientservice.dtos.request;

import jakarta.validation.constraints.Min;
import java.util.List;

public record AuditEntryRequestDTO(
    @Min(value = 0) Integer page,
    @Min(value = 0) Integer size,
//    @NotNull DocumentType documentType,
    List<String> sortBy
) {
    public AuditEntryRequestDTO {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null || sortBy.isEmpty())
            sortBy = List.of(); // empty list
    }
}
