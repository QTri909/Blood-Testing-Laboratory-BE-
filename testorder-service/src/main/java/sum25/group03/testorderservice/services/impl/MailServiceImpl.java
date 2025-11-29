package sum25.group03.testorderservice.services.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void sendPdfAttachmentEmail(
            String to, String subject, String bodyText, byte[] pdfAttachment
    ) {

        try {
            // Implementation for sending email with PDF attachment
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8"
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyText, true);
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
}
