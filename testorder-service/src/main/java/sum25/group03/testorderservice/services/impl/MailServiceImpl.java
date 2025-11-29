package sum25.group03.testorderservice.services.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.services.interfaces.MailService;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPdfAttachmentEmail(
            String toEmail, String receiverName, String subject, byte[] pdfAttachment
    ) {

        try {
            // Implementation for sending email with PDF attachment
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8"
            );

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(buildEmailBody(receiverName), true);
            helper.setFrom(fromEmail);


            // Generate a simple PDF as byte array (for demonstration purposes)
            helper.addAttachment(
                    "test-order-attachment.pdf",
                    new ByteArrayResource(pdfAttachment)
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Messaging exception occurred", e);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }


    }

    private String buildEmailBody(String receiverName) {
        // html:
        return "<p>Dear " + receiverName + ",</p>"
                + "<p>You're receiving test order't results, please check it.</p>"
                + "<p>Best regards,<br/>LIS Team</p>";
    }
}
