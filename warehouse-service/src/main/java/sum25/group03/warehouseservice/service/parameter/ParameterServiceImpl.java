package sum25.group03.warehouseservice.service.parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.response.ParameterRes;
import sum25.group03.warehouseservice.repository.ParameterRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParameterServiceImpl implements ParameterService {
    private final ParameterRepo parameterRepo;

    @Override
    public List<ParameterRes> getAllParameters() {
        return parameterRepo.findAllParameters();
    }
}
