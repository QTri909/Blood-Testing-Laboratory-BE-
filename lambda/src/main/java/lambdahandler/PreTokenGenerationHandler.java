package lambdahandler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CognitoUserPoolPreTokenGenerationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Lambda handler để thêm role + privilege vào token Cognito trước khi trả về client.
 */
public class PreTokenGenerationHandler
        implements RequestHandler<CognitoUserPoolPreTokenGenerationEvent, CognitoUserPoolPreTokenGenerationEvent> {

    private static final String IAM_SERVICE_URL = "http://13.239.30.25:8080/auth/privileges?email=";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CognitoUserPoolPreTokenGenerationEvent handleRequest(
            CognitoUserPoolPreTokenGenerationEvent event, Context context) {

        var request = event.getRequest();
        var response = event.getResponse();

        String email = request.getUserAttributes().get("email");
        context.getLogger().log("PreTokenGeneration: user=" + email);

        try {
            // 1️ Gọi IAM service lấy role + privilege
            String url = IAM_SERVICE_URL + email;
            String resp = Request.get(url)
                    .addHeader("Accept", "application/json")
                    .execute()
                    .returnContent()
                    .asString();


            Map<String, List<String>> data = objectMapper.readValue(resp, Map.class);
            List<String> roles = data.getOrDefault("roles", List.of());
            List<String> privileges = data.getOrDefault("privileges", List.of());

            // 2️ Gán vào claims của token
            Map<String, String> claims = new HashMap<>();
            claims.put("custom:roles", String.join(",", roles));
            claims.put("custom:privileges", String.join(",", privileges));

            var claimsOverride = new CognitoUserPoolPreTokenGenerationEvent.ClaimsOverrideDetails();
            claimsOverride.setClaimsToAddOrOverride(claims);
            response.setClaimsOverrideDetails(claimsOverride);

            context.getLogger().log("Added claims: " + claims);

        } catch (Exception e) {
            context.getLogger().log("Failed to add claims: " + e.getMessage());
        }

        return event;
    }
}
