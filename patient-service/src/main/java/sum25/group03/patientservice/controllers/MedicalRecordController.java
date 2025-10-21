package sum25.group03.patientservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.MurmurHash3;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
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

}
