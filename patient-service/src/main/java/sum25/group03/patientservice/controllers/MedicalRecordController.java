package sum25.group03.patientservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.NewRecordStatusRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.enums.MedicalRecordStatus;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;


import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MedicalRecordResponse> registerMedicalRecord(@Valid @RequestBody MedicalRecordRequest medicalRecordRequest) {
        return ApiResponse.add("Created", medicalRecordService.registerMedicalRecord(medicalRecordRequest));
    }

    @PatchMapping("/assigned-doctor")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UpdatedAssignedDoctor> updateAssignedDoctor(@Valid @RequestBody UpdatedAssignedDoctor updateInfo) {
        return ApiResponse.ok(medicalRecordService.updateAssignedDoctor(updateInfo));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<MedicalRecordResponse>> getAll(@RequestParam Long viewerId) {
        return ApiResponse.ok(medicalRecordService.getAll(viewerId));
    }

    @GetMapping("/{recordId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MedicalRecordResponse> getById(@PathVariable Long recordId, @RequestParam Long viewerId) {
        return ApiResponse.ok(medicalRecordService.getById(recordId, viewerId));
    }

    @DeleteMapping("/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteById(
            @PathVariable Long recordId,
            @RequestHeader("X-User-Id") Long deleterId
    ) {
        NewRecordStatusRequest requestInfo = new NewRecordStatusRequest(recordId, MedicalRecordStatus.DELETED, deleterId);
        medicalRecordService.deleteById(requestInfo);
    }

}
