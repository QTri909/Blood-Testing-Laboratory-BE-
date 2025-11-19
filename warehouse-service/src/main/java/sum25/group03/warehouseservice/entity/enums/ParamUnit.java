package sum25.group03.warehouseservice.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ParamUnit {
    // common
    PERCENTAGE("%"),
    CELSIUS("°C"),
    BPM("bpm"), // beats per minute

    // blood cells:
    CELLS_PER_UL("cells/µL"),
    MILLIONS_PER_UL("millions/µL"),

    // size:
    FL("fL"), // femtoliter

    // others:
    PG("pg"), // picogram
    G_PER_DL("g/dL"); // grams per deciliter
    private final String unit;

    ParamUnit(String unit) {
        this.unit = unit;
    }

    @JsonValue
    public String getUnit() {
        return unit;
    }

    @JsonCreator
    public static ParamUnit fromJson(String value) {
        for (ParamUnit u : values()) {
            if (u.unit.equalsIgnoreCase(value)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Invalid ParamUnit: " + value);
    }
}
