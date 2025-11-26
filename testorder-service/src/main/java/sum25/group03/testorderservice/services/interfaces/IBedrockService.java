package sum25.group03.testorderservice.services.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import sum25.group03.testorderservice.dtos.response.TestResultReviewDTO;

public interface IBedrockService {
    public TestResultReviewDTO reviewTestResult(Long id) throws JsonProcessingException;

    JsonNode extractAiResult(String reviewed) throws JsonProcessingException;
}
