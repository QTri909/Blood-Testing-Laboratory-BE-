package sum25.group03.testorderservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.entity.Parameter;
import sum25.group03.testorderservice.entity.TestResult;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.repository.TestResultRepository;
import sum25.group03.testorderservice.service.interfaces.ITestResult;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TestResultServiceImpl implements ITestResult {

    @Autowired
    private TestResultRepository testResultRepository;

    @Override
    public void reviewTestResult(Long testResultId, Double adjustedValue, Long reviewId){
        TestResult testResult;
        Optional<TestResult> testResultOpt = testResultRepository.findById(testResultId);
        if (testResultOpt.isPresent()) {
            testResult = testResultOpt.get();
        } else {
            throw new EntityNotFoundException("Test result not found");
        }
        if (testResult.getStatus() != TestResultStatus.COMPLETED) {
            throw new IllegalStateException("Only completed results can be reviewed");
        }
        Parameter para = testResult.getParameter();
        Double min = para.getMin();
        Double max = para.getMax();
        if(adjustedValue != null){
            if (min != null && adjustedValue < min)
                throw new IllegalArgumentException("Value below minimum: " + min);
            if (max != null && adjustedValue > max)
                throw new IllegalArgumentException("Value exceeds maximum: " + max);
            testResult.setValue(adjustedValue);
        }
        testResult.setStatus(TestResultStatus.REVIEWED);
        testResult.setUpdatedAt(LocalDateTime.now());
        testResultRepository.save(testResult);
    }
}
