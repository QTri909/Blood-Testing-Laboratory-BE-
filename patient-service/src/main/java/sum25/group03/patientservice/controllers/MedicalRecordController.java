package sum25.group03.patientservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.NewRecordStatusRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.enums.MedicalRecordStatus;
import sum25.group03.patientservice.grpc.TestOrderGrpcClient;
import sum25.group03.patientservice.grpc.TestOrderResponse;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderFullFieldDTO;
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
    public ApiResponse<MedicalRecordResponse> registerMedicalRecord(@NotNull @RequestHeader("X-User-Id") Long creatorId) {
        return ApiResponse.add("Created", medicalRecordService.registerMedicalRecord(creatorId));
    }

    @PatchMapping("/assigned-doctor")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UpdatedAssignedDoctor> updateAssignedDoctor(@Valid @RequestBody UpdatedAssignedDoctor updateInfo) {
        return ApiResponse.ok(medicalRecordService.updateAssignedDoctor(updateInfo));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<MedicalRecordResponse>> getAll(
            @RequestHeader("X-User-Id") Long viewerId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return ApiResponse.ok(medicalRecordService.getAll(page, size, viewerId));
    }

    @GetMapping("/patients/{patientId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<MedicalRecordResponse>> getByPatientId(
            @PathVariable(name = "patientId") Long patientId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return ApiResponse.ok(medicalRecordService.getByPatientId(patientId, page, size));
    }

    @GetMapping("/{recordId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MedicalRecordResponse> getById(
            @PathVariable Long recordId,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
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

    // get all test orders of a medical record by its id: (Grpc call)
    @GetMapping("/{recordId}/test-orders")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<GrpcTestOrderFullFieldDTO>> getByTestOrderId(
            @PathVariable Long recordId,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        return ApiResponse.ok(medicalRecordService.getAllTestOrdersByMedicalRecordId(recordId, viewerId));
    }
}
