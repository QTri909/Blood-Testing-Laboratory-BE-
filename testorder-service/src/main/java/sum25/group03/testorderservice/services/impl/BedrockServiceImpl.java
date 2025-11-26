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

    public TestResultReviewDTO reviewTestResult(Long id) throws JsonProcessingException {
        TestResult testResult = testResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("test result not found"));
        Parameter parameter = testResult.getParameter();
        ObjectNode testInfo = mapper.createObjectNode();
        testInfo.put("name", parameter.getName());
        testInfo.put("value", testResult.getValue());
        testInfo.put("min", parameter.getMin());
        testInfo.put("max", parameter.getMax());
        testInfo.put("unit", parameter.getUnit().name());

        String prompt = """
                You are a medical AI. Analyze the following laboratory test parameters.
                Return ONLY a JSON with fields: abnormalities, severity, summary, recommendation.
                
                Test Results (JSON):
                """ + testInfo.toString();

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
        requestBody.put("max_tokens", 800);
        requestBody.set("messages", messagesArray);

        String body = requestBody.toString();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("anthropic.claude-3-sonnet-20240229-v1:0")
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(body))
                .build();

        InvokeModelResponse response = bedrockClient.invokeModel(request);
        JsonNode bedrockResponse = mapper.readTree(response.body().asUtf8String());
        try {
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

        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse Bedrock JSON: " + ex.getMessage());
        }
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

}
