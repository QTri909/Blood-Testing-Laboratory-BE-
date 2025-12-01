package sum25.group03.testorderservice.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class EnumMapper {
    @Named("enumToString")
    public String toEnumString(Enum<?> e) {
        return (e != null) ? e.name() : null;
    }
}
