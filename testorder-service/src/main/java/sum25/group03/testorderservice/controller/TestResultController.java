package sum25.group03.testorderservice.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.testorderservice.services.impl.TestResultServiceImpl;

@RestController
@RequestMapping("/test-result")
public class TestResultController {

    @Autowired
    private TestResultServiceImpl testResultService;

    @PostMapping("/review-test-result")
    public ResponseEntity<?> reviewTestResult(
            @RequestParam("testResultId") @NotNull Long testResultId,
            @RequestParam("adjustedValue") Double adjustedValue,
            @RequestParam("reviewId") @NotNull Long reviewId
    ) {
        testResultService.reviewTestResult(testResultId, adjustedValue, reviewId);
        return ResponseEntity.ok("✅ Test result reviewed successfully by user " + reviewId);
    }
}
