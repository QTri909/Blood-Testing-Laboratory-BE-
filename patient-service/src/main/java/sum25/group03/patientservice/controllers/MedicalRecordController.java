package sum25.group03.patientservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.MurmurHash3;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.enums.ActionTypeFetures;
import sum25.group03.patientservice.services.impl.ActionLogService;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final ActionLogService actionLogService;

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
        actionLogService.logAction(viewerId, ActionTypeFetures.VIEW_ALL_PATIENT_MEDICAL_RECORDS, null);
        log.info("User {} requested all medical records", viewerId);
        return ResponseEntity.ok(medicalRecordService.getAll());
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponse> getById(@PathVariable Long recordId, @RequestParam Long viewerId) {
        actionLogService.logAction(viewerId, ActionTypeFetures.VIEW_PATIENT_MEDICAL_RECORD_DETAIL, recordId);
        log.info("User {} requested medical record with id {}", viewerId, recordId);
        return ResponseEntity.ok(medicalRecordService.getById(recordId));
    }

    /*
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getByPatientId(patientId));
    }
     */
}
