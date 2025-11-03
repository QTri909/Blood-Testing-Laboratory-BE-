package sum25.group03.payment_service.utils;

import java.util.UUID;

public class UUIDUtils {

    public static boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}