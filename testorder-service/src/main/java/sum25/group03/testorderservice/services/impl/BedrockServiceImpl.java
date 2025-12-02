package sum25.group03.testorderservice.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import sum25.group03.testorderservice.dtos.response.TestResultReviewDTO;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.repositories.TestResultRepository;
import sum25.group03.testorderservice.services.interfaces.IBedrockService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BedrockServiceImpl implements IBedrockService {
    @Autowired
    private BedrockRuntimeClient bedrockClient;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private ObjectMapper mapper;

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY_MS = 1000;

    public TestResultReviewDTO reviewTestResult(Long id) throws JsonProcessingException {
        List<TestResult> testResults = testResultRepository.findByTestOrderId(id);
        if(testResults == null || testResults.size() == 0){
            throw new IllegalArgumentException("test result not found");
        }
        ArrayNode testsArray = mapper.createArrayNode();
        for (TestResult tr : testResults) {
            System.out.println("size" + testResults.size());
            Parameter p = tr.getParameter();
            double value = tr.getValue();
            String formattedValue = String.format("%.2f", value);
            ObjectNode testInfo = mapper.createObjectNode();
            testInfo.put("name", p.getName());
            testInfo.put("value", formattedValue);
            testInfo.put("min", p.getMin());
            testInfo.put("max", p.getMax());
            testInfo.put("unit", p.getUnit().name());
            testsArray.add(testInfo);
        }

        String prompt = """
You are a medical AI. Analyze the following laboratory test parameters.

Return EXACTLY ONE JSON object.
No explanation. No markdown.

Format:
{
  "abnormalities": [],
  "severity": "",
  "summary": "",
  "recommendation": ""
}

Test Results (JSON):
""" + testsArray;

        ObjectNode textNode = mapper.createObjectNode();
        textNode.put("type", "text");
        textNode.put("text", prompt);

        ArrayNode contentArray = mapper.createArrayNode();
        contentArray.add(textNode);

        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.set("content", contentArray);

        ArrayNode messagesArray = mapper.createArrayNode();
        messagesArray.add(message);

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("anthropic_version", "bedrock-2023-05-31");
        requestBody.put("max_tokens", 8000);
        requestBody.set("messages", messagesArray);

        String body = requestBody.toString();

        return invokeBedrockWithRetry(body);
    }

    public JsonNode extractAiResult(String responseBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(responseBody);

        String textContent = root
                .path("content")
                .get(0)
                .path("text")
                .asText();

        JsonNode aiJson = mapper.readTree(textContent);

        return aiJson;
    }

    private List<String> parseAbnormalities(JsonNode result) throws JsonProcessingException {
        JsonNode abnormalitiesNode = result.get("abnormalities");

        if (abnormalitiesNode == null || abnormalitiesNode.isMissingNode() || abnormalitiesNode.isNull()) {
            return new ArrayList<>();
        }

        if (abnormalitiesNode.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : abnormalitiesNode) {
                list.add(item.asText());
            }
            return list;
        }
        if (abnormalitiesNode.isTextual()) {
            String raw = abnormalitiesNode.asText();
            JsonNode parsed = mapper.readTree(raw);

            if (parsed.isArray()) {
                List<String> list = new ArrayList<>();
                for (JsonNode item : parsed) {
                    list.add(item.asText());
                }
                return list;
            }
        }
        return new ArrayList<>();
    }

    private TestResultReviewDTO invokeBedrockWithRetry(String body) throws JsonProcessingException {
        int retries = 0;
        long delay = INITIAL_DELAY_MS;

        while (retries < MAX_RETRIES) {
            try {
                InvokeModelRequest request = InvokeModelRequest.builder()
                        .modelId("anthropic.claude-3-sonnet-20240229-v1:0")
                        .contentType("application/json")
                        .accept("application/json")
                        .body(SdkBytes.fromUtf8String(body))
                        .build();

                InvokeModelResponse response = bedrockClient.invokeModel(request);

                // Parse response
                JsonNode bedrockResponse = mapper.readTree(response.body().asUtf8String());
                String aiJsonString = bedrockResponse
                        .get("content")
                        .get(0)
                        .get("text")
                        .asText();

                JsonNode result = mapper.readTree(aiJsonString);

                TestResultReviewDTO dto = new TestResultReviewDTO();
                List<String> abnormalList = parseAbnormalities(result);
                dto.setAbnormalities(abnormalList);
                dto.setSeverity(result.get("severity").asText());
                dto.setSummary(result.get("summary").asText());
                dto.setRecommendation(result.get("recommendation").asText());

                return dto;

            } catch (software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException ex) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    throw new RuntimeException("Max retries exceeded due to throttling", ex);
                }

                System.out.println("Throttled! Retry " + retries + "/" + MAX_RETRIES +
                        " after " + delay + "ms");

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }

                // Exponential backoff: 1s -> 2s -> 4s
                delay *= 2;

            } catch (Exception ex) {
                throw new RuntimeException("Failed to parse Bedrock JSON: " + ex.getMessage(), ex);
            }
        }

        throw new RuntimeException("Failed to invoke Bedrock after retries");
    }

}
