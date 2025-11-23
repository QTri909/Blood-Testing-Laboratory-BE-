package sum25.group03.warehouseservice.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;

public enum ReagentUnit {
    ML("mL"),
    L("L"),
    G("g"),
    KG("kg");
    private final String unit;
    ReagentUnit(String unit) {
        this.unit = unit;
    }

    @JsonValue
    public String getUnit() {
        return unit;
    }
    @JsonCreator
    public static ReagentUnit fromJson(String value) {
        for (ReagentUnit u : values()) {
            if (u.unit.equalsIgnoreCase(value)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Invalid ParamUnit: " + value);
    }
    public static List<String> getAllUnits() {
        List<String> list = new ArrayList<>();
        for (ReagentUnit u: values()) {
            list.add(u.unit);
        }
        return list;
    }
}
