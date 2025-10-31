package sum25.group03.testorderservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParameterUnit {
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
}
