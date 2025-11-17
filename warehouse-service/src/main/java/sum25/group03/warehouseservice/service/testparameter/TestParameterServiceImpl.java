package sum25.group03.warehouseservice.service.testparameter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.warehouseservice.dto.response.GlobalTestParameterRes;
import sum25.group03.warehouseservice.dto.response.NormalRangeRes;
import sum25.group03.warehouseservice.entity.GlobalParameterConfiguration;
import sum25.group03.warehouseservice.entity.GlobalTest;
import sum25.group03.warehouseservice.entity.NormalRange;
import sum25.group03.warehouseservice.entity.TestParameter;
import sum25.group03.warehouseservice.entity.enums.TestType;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.GlobalTestParameterRepo;
import sum25.group03.warehouseservice.repository.TestParamRepo;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestParameterServiceImpl implements  TestParameterService {
    private final GlobalTestParameterRepo globalTestParameterRepo;
    private final TestParamRepo testParamRepo;

    @Override
    public List<GlobalTestParameterRes> getGlobalTestParameters(TestType testType) {
        List<GlobalParameterConfiguration> gpcs = globalTestParameterRepo.findByTestType(testType);

        List<Long> tpIds = gpcs.stream()
                .flatMap(gpc -> gpc.getGlobalTests().stream())
                .map(gt -> gt.getTestParameter().getId())
                .collect(Collectors.toList());

        List<TestParameter> tps = testParamRepo.findAllByIdsWithNormalRanges(tpIds);
        Map<Long, TestParameter> tpMap = tps.stream().collect(Collectors.toMap(TestParameter::getId, Function.identity()));

        return gpcs.stream()
                .flatMap(gpc -> gpc.getGlobalTests().stream()
                        .map(gt -> {
                            TestParameter tp = tpMap.get(gt.getTestParameter().getId());
                            List<NormalRangeRes> nrRes = tp.getNormalRanges().stream()
                                    .map(n -> NormalRangeRes.builder()
                                            .minValue(n.getMinValue())
                                            .maxValue(n.getMaxValue())
                                            .unit(n.getUnit())
                                            .gender(n.getGender())
                                            .build())
                                    .toList();

                            return GlobalTestParameterRes.builder()
                                    .globalTestParameterId(gpc.getId())
                                    .id(tp.getId())
                                    .parameterName(tp.getParameterName())
                                    .abbreviation(tp.getAbbreviation())
                                    .description(tp.getDescription())
                                    .price(tp.getPrice())
                                    .normalRange(nrRes)
                                    .build();
                        })
                )
                .toList();
    }








}





