package lambdahandler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.fluent.Request;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PreTokenGenerationHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String IAM_SERVICE_URL = "http://13.239.30.25:8080/auth/privileges?email=";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        try {
            JsonNode root = objectMapper.convertValue(event, JsonNode.class);
            ObjectNode responseNode = (ObjectNode) root.with("response");
            JsonNode requestNode = root.path("request");
            String email = requestNode.path("userAttributes").path("email").asText();

            context.getLogger().log("PreTokenGeneration (V2) started for: " + email + "\n");

            // 1 Gọi IAM service
            String url = IAM_SERVICE_URL + email;
            String resp = Request.get(url)
                    .addHeader("Accept", "application/json")
                    .execute()
                    .returnContent()
                    .asString();

            Map<String, List<String>> data = objectMapper.readValue(resp, new TypeReference<>() {});
            List<String> roles = data.getOrDefault("roles", List.of());
            List<String> privileges = data.getOrDefault("privileges", List.of());

            // 2 Tạo claims
            ObjectNode claims = objectMapper.createObjectNode();
            claims.put("roles", String.join(",", roles));
            claims.put("privileges", String.join(",", privileges));

            // 3 Chuẩn bị cấu trúc chuẩn V2:
            // response.claimsAndScopeOverrideDetails.accessTokenGeneration.claimsToAddOrOverride
            ObjectNode claimsAndScope = objectMapper.createObjectNode();
            ObjectNode accessTokenGen = objectMapper.createObjectNode();
            accessTokenGen.set("claimsToAddOrOverride", claims);

            ObjectNode idTokenGen = objectMapper.createObjectNode();
            idTokenGen.set("claimsToAddOrOverride", claims);

            claimsAndScope.set("accessTokenGeneration", accessTokenGen);
            claimsAndScope.set("idTokenGeneration", idTokenGen);

            // 4 Add vào response
            responseNode.set("claimsAndScopeOverrideDetails", claimsAndScope);

            context.getLogger().log(" Added custom claims to tokens: " + claims.toPrettyString() + "\n");

            return objectMapper.convertValue(root, new TypeReference<>() {});
        } catch (Exception e) {
            context.getLogger().log(" Error: " + e.getMessage() + "\n");
            for (StackTraceElement ste : e.getStackTrace()) {
                context.getLogger().log(ste.toString() + "\n");
            }
            return event;
        }
    }
}
