package sum25.group03.patientservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
//import sum25.group03.patientservice.grpc.TestOrderResponse;
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
    public List<PatientResponseDTO> getAllPatients(
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) Integer size,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page
    ) {
        return patientService.getAllPatientsWith(size, page);
    }

    @GetMapping("/test-orders/latest/{patientId}")
    @ResponseStatus(HttpStatus.OK)
    public Object getPatientById(
            @PathVariable(name = "patientId") Long patientId
    ) {
        GrpcTestOrderDTO searchedTestOrder = patientService.getLatestByPatientId(patientId);
        if (searchedTestOrder == null) {
            return "No test order found for patient with ID: " + patientId;
        }
        return searchedTestOrder;
    }
}
