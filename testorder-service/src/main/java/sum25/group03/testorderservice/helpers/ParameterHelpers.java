package sum25.group03.testorderservice.helpers;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.repositories.ParameterRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ParameterHelpers {

    private final ParameterRepository parameterRepository;

    // load all parameter code with its price into a map:
    public Map<String, Long> loadParameterCodeWithPriceMap() {
        // load all parameter:
        List<Parameter> allParameters = parameterRepository.findAll();
        // init map bucket:
        Map<String, Long> priceMapping = new HashMap<>();
        for (Parameter parameter: allParameters) {
            priceMapping.put(parameter.getParamCode(), parameter.getPrice());
        }
        return priceMapping;
    }

    public Map<Long, Long> loadParameterIdWithPriceMap() {
        // load all parameter:
        List<Parameter> allParameters = parameterRepository.findAll();
        // init map bucket:
        Map<Long, Long> priceMapping = new HashMap<>();
        for (Parameter parameter: allParameters) {
            priceMapping.put(parameter.getId(), parameter.getPrice());
        }
        return priceMapping;
    }
}
