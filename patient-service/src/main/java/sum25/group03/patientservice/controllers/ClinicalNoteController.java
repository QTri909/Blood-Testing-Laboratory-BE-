package sum25.group03.patientservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.patientservice.dtos.request.ClinicalNoteRequest;
import sum25.group03.patientservice.dtos.response.ClinicalNoteResponse;
import sum25.group03.patientservice.services.interfaces.ClinicalNoteService;

import java.util.List;

@RestController
@RequestMapping("/api/clinical-notes")
@RequiredArgsConstructor
public class ClinicalNoteController {

    private final ClinicalNoteService service;

    @PostMapping
    public ResponseEntity<ClinicalNoteResponse> create(@RequestBody ClinicalNoteRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ClinicalNoteResponse> update(@PathVariable Long noteId,
                                                       @RequestBody ClinicalNoteRequest request) {
        return ResponseEntity.ok(service.update(noteId, request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> delete(@PathVariable Long noteId) {
        service.delete(noteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ClinicalNoteResponse> getById(@PathVariable Long noteId) {
        return ResponseEntity.ok(service.getById(noteId));
    }

    @GetMapping("/record/{recordId}")
    public ResponseEntity<List<ClinicalNoteResponse>> getByRecordId(@PathVariable Long recordId) {
        return ResponseEntity.ok(service.getByRecordId(recordId));
    }
}
