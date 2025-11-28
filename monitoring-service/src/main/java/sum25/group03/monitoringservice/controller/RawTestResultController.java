package sum25.group03.monitoringservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.monitoringservice.dto.PagedResponse;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.service.RawTestResultService;

@RestController
@RequestMapping("/api/v1/raw-tests")
public class RawTestResultController {

    private final RawTestResultService service;

    public RawTestResultController(RawTestResultService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PagedResponse> getRawTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String testOrderId,
            @RequestParam(required = false) String instrumentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        Page<RawTestResult> pageResult =
                service.getFiltered(page, size, testOrderId, instrumentId, status, barcode, from, to);

        return ApiResponse.add("Fetched raw test results successfully",
                PagedResponse.fromPage(pageResult));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getRawTestById(@PathVariable String id) {
        return service.getById(id)
                .map(result -> ApiResponse.add("Fetched raw test result successfully", result))
                .orElse(ApiResponse.error(HttpStatus.NOT_FOUND, "Raw test result not found", "/api/v1/raw-tests/" + id));
    }
}