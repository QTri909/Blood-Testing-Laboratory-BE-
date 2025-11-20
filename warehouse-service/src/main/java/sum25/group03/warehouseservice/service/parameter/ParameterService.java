package sum25.group03.warehouseservice.service.parameter;

import sum25.group03.warehouseservice.dto.response.ParameterRes;

import java.util.List;

public interface ParameterService {
    List<ParameterRes> getAllParameters();
    List<String> getAllParameterUnits();
}
