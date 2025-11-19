package sum25.group03.testorderservice.services.impl;

import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.NonStreamedChatResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.configs.CohereConfig;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.ICohereService;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CohereServiceImpl implements ICohereService {

    @Autowired
    private CohereConfig cohereConfig;

    @Autowired
    private TestResultRepository testResultRepository;

    private final String MODEL = "command-a-03-2025";

    public String jugeReview(Long testResultId) {
        TestResult result = testResultRepository.findById(testResultId)
                .orElseThrow(() -> new EntityNotFoundException("Test result not found"));
        Parameter parameter = result.getParameter();
        String message = String.format("""
                You are an experienced medical doctor. Review this lab test result:
                Test Name: %s
                Test Value: %s
                Unit: %s
                Reference Range: %.2f - %.2f

                Provide a brief professional interpretation in 2-4 sentences.
                """, result.getTestType(), result.getValue(), parameter.getUnit(), parameter.getMin(), parameter.getMax());
        ChatRequest request = ChatRequest.builder()
                .message(message)
                .model(MODEL)
                .build();
        NonStreamedChatResponse response = cohereConfig.cohereClient().chat(request);
        String review = response.getText();
        result.setReview(review);
        result.setStatus(TestResultStatus.AI_REVIEWED);
        result.setUpdatedAt(LocalDateTime.now());
        log.info("Test result ID: " + result.getId()
                +"\nStatus: " + result.getStatus()
                + "\nTimestamp: " + LocalDateTime.now());
        testResultRepository.save(result);

        return review;
    }
}
