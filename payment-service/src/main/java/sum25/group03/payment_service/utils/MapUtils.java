package sum25.group03.payment_service.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MapUtils {

    private static final Gson GSON = new Gson();

    public static Map<String, Object> toObjectMap(Map<String, String> stringMap) {
        if (stringMap == null) return null;
        String jsonStr = GSON.toJson(stringMap);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return GSON.fromJson(jsonStr, type);
    }
}
