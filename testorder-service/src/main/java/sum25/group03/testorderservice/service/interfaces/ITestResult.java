package sum25.group03.testorderservice.service.interfaces;

import org.springframework.data.crossstore.ChangeSetPersister;

public interface ITestResult {
    public void reviewTestResult(Long testResultId, Double adjustedValue, Long reviewId);
}
