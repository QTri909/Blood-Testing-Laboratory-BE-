package sum25.group03.iamservice.service.Interface;

public interface RedisService {
    void saveValue(String key, String value, long expirationInSeconds);
    String getValue(String key);
    void deleteValue(String key);
}
