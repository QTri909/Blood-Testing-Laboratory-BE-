package sum25.group03.payment_service.helpers;

public class EmailHelpers {

    // template body for sending vnpay payment request email to patient
    public static String getVnPayPaymentEmailBody(String patientName, String paymentUrl) {
        return "<html>" +
                "<body>" +
                "<p>Dear " + patientName + ",</p>" +
                "<p>Thank you for choosing our healthcare services. To complete your payment, please click the link below:</p>" +
                "<a href=\"" + paymentUrl + "\">Complete Payment</a>" +
                "<p>If you did not initiate this request, please ignore this email.</p>" +
                "<p>Best regards,<br/>Healthcare Team</p>" +
                "</body>" +
                "</html>";
    }
}
