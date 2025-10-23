package sum25.group03.patientservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.enums.ActionTypeFeatures;
import sum25.group03.patientservice.services.impl.ActionLogService;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;


import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordResponse registerMedicalRecord(@Valid @RequestBody MedicalRecordRequest medicalRecordRequest) {
        return medicalRecordService.registerMedicalRecord(medicalRecordRequest);
    }

    @PatchMapping("/assigned-doctor")
    @ResponseStatus(HttpStatus.OK)
    public UpdatedAssignedDoctor updateAssignedDoctor(@Valid @RequestBody UpdatedAssignedDoctor updateInfo) {
        return medicalRecordService.updateAssignedDoctor(updateInfo);
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAll(@RequestParam Long viewerId) {
        return ResponseEntity.ok(medicalRecordService.getAll(viewerId));
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponse> getById(@PathVariable Long recordId, @RequestParam Long viewerId) {
        return ResponseEntity.ok(medicalRecordService.getById(recordId, viewerId));
    }

    /*
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getByPatientId(patientId));
    }
     */
}
