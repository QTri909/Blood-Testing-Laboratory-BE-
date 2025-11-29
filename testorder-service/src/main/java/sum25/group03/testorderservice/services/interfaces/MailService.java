package sum25.group03.testorderservice.services.interfaces;

public interface MailService {

    void sendPdfAttachmentEmail(
            String to, String subject, String bodyText, byte[] pdfAttachment
    );
}
