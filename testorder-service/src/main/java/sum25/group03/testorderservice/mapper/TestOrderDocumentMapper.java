package sum25.group03.testorderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.entities.mongodb.TemplateParameter;

@Mapper(componentModel = "spring", uses = EnumMapper.class)
public interface TestOrderDocumentMapper {
    @Mapping(target = "status", source = "status", qualifiedByName = "enumToString")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "enumToString")
    TemplateParameter toTemplateParameter(Parameter parameter);
}
