package sum25.group03.payment_service.services.interfaces;

public interface QRCodeService {
    record QR(String base64, String dataUrl) {}
    QR generate(String content, int width, int height);
}
