package sum25.group03.common.response.services.interfaces;

public interface CommonRedisService {
    void saveValue(String key, String value, long expirationInSeconds);
    String getValue(String key);
    void deleteValue(String key);
}
