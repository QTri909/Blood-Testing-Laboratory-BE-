package sum25.group03.testorderservice.services.interfaces;

public interface MailService {

    void sendPdfAttachmentEmail(
            String toEmail, String receiverName, String subject, byte[] pdfAttachment
    );
}
