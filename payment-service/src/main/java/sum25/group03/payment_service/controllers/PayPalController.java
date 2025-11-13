package sum25.group03.payment_service.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.payment_service.services.interfaces.PayPalService;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;

@RestController
@RequestMapping("/api/v1/paypal")
@RequiredArgsConstructor
@Slf4j
public class PayPalController {

    private final PayPalService payPalService;
    private final PaymentTransactionService paymentTransactionService;

    @Value("${frontend.url}")
    private String frontEndUrl;

//    @GetMapping("/return")
//    @ResponseStatus(HttpStatus.OK)
//    public ApiResponse<String> handleReturn(@RequestParam("token") String token,
//                                            @RequestParam(value = "PayerID", required = false) String payerId) {
//        log.info("PayPal RETURN callback received: token={}, payerId={}", token, payerId);
//        String result = payPalService.capturePayment(token);
//        paymentTransactionService.captureAndUpdateStatus(token);
//
//        return ApiResponse.add("Payment success", result);
//    }

    @GetMapping("/return")
    public RedirectView handleReturn(@RequestParam("token") String token,
                                     @RequestParam(value = "PayerID", required = false) String payerId) {
        log.info("PayPal RETURN callback received: token={}, payerId={}", token, payerId);

        try {
            // 1️⃣ Capture payment and update DB
            String result = payPalService.capturePayment(token);
            paymentTransactionService.captureAndUpdateStatus(token);

            // 2️⃣ Parse the PayPal response JSON
            JsonObject json = JsonParser.parseString(result).getAsJsonObject();

            String status = json.has("status") ? json.get("status").getAsString() : "UNKNOWN";
            String transactionId = json.has("id") ? json.get("id").getAsString() : token;

            String orderCode = "default";
            if (json.has("purchase_units") && json.get("purchase_units").isJsonArray()) {
                JsonObject firstUnit = json.getAsJsonArray("purchase_units").get(0).getAsJsonObject();
                if (firstUnit.has("reference_id")) {
                    orderCode = firstUnit.get("reference_id").getAsString();
                }
            }

            // 3️⃣ Build redirect URL for frontend
            String redirectUrl = String.format(
                    "%s/payment/result?status=%s&order_code=%s&transaction_id=%s",
                    frontEndUrl,
                    status,
                    orderCode,
                    transactionId
            );

            log.info("Redirecting user to frontend result page: {}", redirectUrl);
            return new RedirectView(redirectUrl);

        } catch (Exception e) {
            log.error("Error handling PayPal return: {}", e.getMessage(), e);
            return new RedirectView(frontEndUrl + "/payment/result?status=FAILED");
        }
    }



    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> handleCancel() {
        return ApiResponse.add("Payment cancelled", null);
    }
}
