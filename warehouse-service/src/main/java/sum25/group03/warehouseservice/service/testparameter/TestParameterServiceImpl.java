package sum25.group03.warehouseservice.service.testparameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.common.response.dtos.grpc.ParameterGrpc;
import sum25.group03.testorder.grpc.SyncParameterResponse;
import sum25.group03.warehouseservice.dto.request.TestParameterReq;
import sum25.group03.warehouseservice.dto.request.TestTemplateReq;
import sum25.group03.warehouseservice.dto.response.GlobalTestParameterRes;
import sum25.group03.warehouseservice.dto.response.NewTestTemplate;
import sum25.group03.warehouseservice.dto.response.NormalRangeRes;
import sum25.group03.warehouseservice.dto.response.TestParameterRes;
import sum25.group03.warehouseservice.entity.GlobalParameterConfiguration;
import sum25.group03.warehouseservice.entity.GlobalTest;
import sum25.group03.warehouseservice.entity.NormalRange;
import sum25.group03.warehouseservice.entity.TestParameter;
import sum25.group03.warehouseservice.entity.enums.ParameterStatus;
import sum25.group03.warehouseservice.entity.enums.TestType;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.grpc.TestOrderGrpcClient;
import sum25.group03.warehouseservice.mapper.NormalRangeMapper;
import sum25.group03.warehouseservice.repository.GlobalTestParameterRepo;
import sum25.group03.warehouseservice.repository.TestParamRepo;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestParameterServiceImpl implements  TestParameterService {
    private final GlobalTestParameterRepo globalTestParameterRepo;
    private final TestParamRepo testParamRepo;
    private final NormalRangeMapper normalRangeMapper;
    private final TestOrderGrpcClient testOrderGrpcClient;

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

    @Override
    @Transactional
    public NewTestTemplate addTestTemplate(TestTemplateReq testTemplate) {
        List<TestParameter> testParameters = testParamRepo.findAllByIdInAndStatus(testTemplate.getId(), ParameterStatus.ACTIVE);
        if (testParameters.size() != testTemplate.getId().size()) {
            throw new NotFoundException("One or more Test Parameters not found or inactive");
        }

        GlobalParameterConfiguration gpc = GlobalParameterConfiguration.builder()
                .testType(testTemplate.getTestType())
                .description(testTemplate.getDescription() != null ? testTemplate.getDescription() : "")
                .active(true)
                .build();

        List<GlobalTest> globalTests = testParameters.stream()
                .map(tp -> GlobalTest.builder()
                        .testParameter(tp)
                        .globalParameterConfiguration(gpc)
                        .build())
                //.collect(Collectors.toList());
                        .toList();

        gpc.setGlobalTests(globalTests);

        GlobalParameterConfiguration saved = globalTestParameterRepo.save(gpc);
        List<TestParameterRes> tpRes = testParameters.stream()
                .map(tp -> TestParameterRes.builder()
                        .id(tp.getId())
                        .parameterName(tp.getParameterName())
                        .abbreviation(tp.getAbbreviation())
                        .description(tp.getDescription())
                        .price(tp.getPrice())
                        .normalRange(normalRangeMapper.toResponse(tp.getNormalRanges()))
                        .build())
                .toList();

        return NewTestTemplate.builder()
                .globalTestParameterId(saved.getId())
                .testType(saved.getTestType())
                .testParameters(tpRes)
                .build();
    }

    @Override
    public TestParameterRes addTestParameter(TestParameterReq req) {

        TestParameter testParameter = TestParameter.builder()
                .parameterName(req.getParameterName())
                .abbreviation(req.getAbbreviation())
                .description(req.getDescription() != null ? req.getDescription() : "")
                .price(req.getPrice())
                .status(ParameterStatus.ACTIVE)
                .build();
        List<NormalRange> normalRanges = req.getNormalRange().stream()
                .map(nrReq -> NormalRange.builder()
                        .minValue(nrReq.getMinValue())
                        .maxValue(nrReq.getMaxValue())
                        .unit(nrReq.getUnit())
                        .gender(nrReq.getGender())
                        .testParameter(testParameter)
                        .build())
                .toList();
        testParameter.setNormalRanges(normalRanges);
        TestParameter saved = testParamRepo.save(testParameter);

        List<ParameterGrpc> parameterGrpcList = saved.getNormalRanges().stream()
                .map(nr -> ParameterGrpc.builder()
                        .id(saved.getId())
                        .abbreviation(saved.getAbbreviation())
                        .parameterName(saved.getParameterName())
                        .price(saved.getPrice())
                        .description(saved.getDescription() != null ? saved.getDescription() : "")
                        .minValue(nr.getMinValue())
                        .maxValue(nr.getMaxValue())
                        .unit(nr.getUnit().name())
                        .gender(nr.getGender().toString())
                        .build()
                ).toList();
        SyncParameterResponse response = testOrderGrpcClient.syncParameter(parameterGrpcList);
        log.info("Sync Parameter Response: {}", response.getSuccess());

        TestParameterRes result = TestParameterRes.builder()
                .id(saved.getId())
                .parameterName(saved.getParameterName())
                .abbreviation(saved.getAbbreviation())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .normalRange(normalRangeMapper.toResponse(saved.getNormalRanges()))
                .build();
        return result;
    }

    @Override
    public List<TestParameterRes> getAllTestParameter() {
        List<TestParameter> testParameters = testParamRepo.findAllByStatus(ParameterStatus.ACTIVE);
        return testParameters.stream()
                .map(tp -> TestParameterRes.builder()
                        .id(tp.getId())
                        .parameterName(tp.getParameterName())
                        .abbreviation(tp.getAbbreviation())
                        .description(tp.getDescription())
                        .price(tp.getPrice())
                        .normalRange(normalRangeMapper.toResponse(tp.getNormalRanges()))
                        .build())
                .toList();
    }

}





