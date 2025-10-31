package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.request.ClinicalNoteRequest;
import sum25.group03.patientservice.dtos.response.ClinicalNoteResponse;

import java.util.List;

public interface ClinicalNoteService {
    ClinicalNoteResponse create(ClinicalNoteRequest request);
    ClinicalNoteResponse update(Long noteId, ClinicalNoteRequest request);
    void delete(Long noteId);
    ClinicalNoteResponse getById(Long noteId);
    List<ClinicalNoteResponse> getByRecordId(Long recordId);
}
