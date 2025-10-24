package sum25.group03.patientservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.patientservice.dtos.request.AuditEntryRequestDTO;
import sum25.group03.patientservice.dtos.response.AuditEntryResponseDTO;
import sum25.group03.patientservice.enums.DocumentType;
import sum25.group03.patientservice.services.interfaces.AuditEntryMongoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-entries")
@RequiredArgsConstructor
public class AuditEntryController {

    private final AuditEntryMongoService auditEntryService;

    private Sort buildSortCriteria(List<String> sortBy) {

        if (sortBy.isEmpty()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }

        return Sort.by(sortBy.stream()
                .map(field -> {
                    if (field.equals("createdAt")) {
                        return Sort.Order.desc(field);
                    }
                    return Sort.Order.asc(field);
                })
                .toList()
        );
    }

    @GetMapping("/{documentType}")
    @ResponseStatus(HttpStatus.OK)
    public Page<AuditEntryResponseDTO> listAuditEntriesWithPagination(
            @PathVariable DocumentType documentType,
            @ModelAttribute AuditEntryRequestDTO auditEntryRequestDTO,
            @RequestHeader("X-Viewer-Id") Long viewerId
    ) {
        // build sort criteria
        Sort criteria = buildSortCriteria(auditEntryRequestDTO.sortBy());

        // build pageable object
        Pageable pageable = PageRequest.of(
                auditEntryRequestDTO.page(),
                auditEntryRequestDTO.size(),
                criteria
        );

        return auditEntryService.queryLogsWithPagination(
                pageable, documentType, viewerId);
    }
}
