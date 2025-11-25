package sum25.group03.patientservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
//import sum25.group03.patientservice.grpc.TestOrderResponse;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderDTO;
import sum25.group03.patientservice.services.interfaces.PatientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    private static final String DEFAULT_SIZE = "10";
    private static final String DEFAULT_PAGE = "0";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<UserSnapshotResponse>> getAllPatients(
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) Integer size,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page
    ) {
        return ApiResponse.ok(patientService.getAllPatientsWith(size, page));
    }

    @GetMapping("exists")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<UserSnapshotResponse>> getAllExistingPatients( // not 'DELETED'
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) Integer size,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page
    ) {
        return ApiResponse.ok(patientService.getAllExistingPatientsWith(size, page));
    }

    @GetMapping("iam")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<PatientResponseDTO>> getAllPatientsIAM(
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) Integer size,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page
    ) {
        return ApiResponse.ok(patientService.getAllIAMPatientsWith(size, page));
    }

    @GetMapping("/{patientId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserSnapshotResponse> getPatientByIdIAM(
            @PathVariable(name = "patientId") Long patientId,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        return ApiResponse.add("Fetched patient with ID: " + patientId,
                patientService.getPatientByExternalUserId(patientId, viewerId));
    }

    @GetMapping("/test-orders/{patientId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> getLatestTestOrderOfPatientById(
            @PathVariable(name = "patientId") Long patientId
    ) {
        GrpcTestOrderDTO searchedTestOrder = patientService.getLatestByPatientId(patientId);
        if (searchedTestOrder == null) {
            return ApiResponse.ok("No test order found for patient with ID: " + patientId, null);
        }
        return ApiResponse.ok(searchedTestOrder);
    }
}
