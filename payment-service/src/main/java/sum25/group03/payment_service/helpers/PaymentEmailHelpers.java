package sum25.group03.payment_service.helpers;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sum25.group03.payment_service.dtos.request.PaymentEmailHelperDTO;
import sum25.group03.payment_service.services.interfaces.EmailService;

@Component
@RequiredArgsConstructor
@Builder
public class PaymentEmailHelpers {

    private final EmailService emailService;

    public void sendPaymentHtmlEmail(String subject, PaymentEmailHelperDTO paymentEmailHelperDTO) throws MessagingException {
        String body = getPaymentEmailBody(
                paymentEmailHelperDTO.getReceiverName(),
                paymentEmailHelperDTO.getPaymentUrl(),
                paymentEmailHelperDTO.getAdditionalInfo()
        );
        emailService.sendHtmlEmail(paymentEmailHelperDTO.getReceiverEmail(), subject, body);
    }

    // template body for sending vnpay payment request email to patient
    public static String getPaymentEmailBody(String patientName, String paymentUrl, String additionalInfo) {
        return "<html>" +
                "<body>" +
                "<p>Dear " + patientName + ",</p>" +
                "<p>Thank you for choosing our healthcare services. To complete your payment, please click the link below:</p>" +
                "<p>" + additionalInfo + "</p>" +
                "<a href=\"" + paymentUrl + "\">Complete Payment</a>" +
                "<p>If you did not initiate this request, please ignore this email.</p>" +
                "<p>Best regards,<br/>Healthcare Team</p>" +
                "</body>" +
                "</html>";
    }
}
