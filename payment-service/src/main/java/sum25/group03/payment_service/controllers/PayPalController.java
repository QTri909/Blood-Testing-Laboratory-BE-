package sum25.group03.payment_service.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.payment_service.services.interfaces.PayPalService;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
@Slf4j
public class PayPalController {

    private final PayPalService payPalService;
    private final PaymentTransactionService paymentTransactionService;


    @GetMapping("/return")
    public ResponseEntity<String> handleReturn(@RequestParam("token") String token,
                                               @RequestParam(value = "PayerID", required = false) String payerId) {
        log.info("PayPal RETURN callback received: token={}, payerId={}", token, payerId);
        String result = payPalService.capturePayment(token);
        paymentTransactionService.captureAndUpdateStatus(token);

        return ResponseEntity.ok("Payment success! Result: " + result);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {
        return ResponseEntity.ok("Payment cancelled.");
    }
}
