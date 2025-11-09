package sum25.group03.patientservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.patientservice.dtos.request.ClinicalNoteRequest;
import sum25.group03.patientservice.dtos.response.ClinicalNoteResponse;
import sum25.group03.patientservice.services.interfaces.ClinicalNoteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clinical-notes")
@RequiredArgsConstructor
public class ClinicalNoteController {

    private final ClinicalNoteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClinicalNoteResponse> create(@RequestBody ClinicalNoteRequest request) {
        return ApiResponse.add("Created", service.create(request));
    }

    @PutMapping("/{noteId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ClinicalNoteResponse> update(@PathVariable Long noteId,
                                                       @RequestBody ClinicalNoteRequest request) {
        return ApiResponse.ok(service.update(noteId, request));
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long noteId) {
        service.delete(noteId);
    }

    @GetMapping("/{noteId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ClinicalNoteResponse> getById(@PathVariable Long noteId) {
        return ApiResponse.ok(service.getById(noteId));
    }

    @GetMapping("/record/{recordId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ClinicalNoteResponse>> getByRecordId(@PathVariable Long recordId) {
        return ApiResponse.ok(service.getByRecordId(recordId));
    }
}
