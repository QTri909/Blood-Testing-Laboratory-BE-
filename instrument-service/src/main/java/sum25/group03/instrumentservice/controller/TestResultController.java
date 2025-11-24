package sum25.group03.instrumentservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.instrumentservice.controller.response.RawTestResultPageResponse;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;
import sum25.group03.instrumentservice.service.RawTestResultService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instruments/test-results")
@RequiredArgsConstructor
@Tag(name = "Test result management", description = "APIs for managing test results associated with instruments")
public class TestResultController {
    private final RawTestResultService rawTestResultService;
    @GetMapping("/instrument-test-results/{instrumentId}")
    public ResponseEntity<RawTestResultPageResponse> getTestResultsByInstrumentId(@PathVariable Long instrumentId,
                                                                                  @RequestParam(defaultValue = "1") int page,
                                                                                  @RequestParam(defaultValue = "4") int size) {
        return ResponseEntity.ok(rawTestResultService.getResultFromInstrumentId(instrumentId, page, size));
    }

    @DeleteMapping("/delete-test-result/{resultId}")
    public ResponseEntity<Boolean> deleteRawTestResult(@PathVariable Long resultId) {
        boolean deleted = rawTestResultService.deleteById(resultId);
        return ResponseEntity.ok(deleted);
    }

}
