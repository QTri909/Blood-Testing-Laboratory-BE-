package sum25.group03.warehouseservice.service.testparameter;

import sum25.group03.warehouseservice.dto.request.TestParameterReq;
import sum25.group03.warehouseservice.dto.request.TestTemplateReq;
import sum25.group03.warehouseservice.dto.request.UnusedTestParameterReq;
import sum25.group03.warehouseservice.dto.response.GlobalTestParameterRes;
import sum25.group03.warehouseservice.dto.response.NewTestTemplate;
import sum25.group03.warehouseservice.dto.response.TestParameterRes;
import sum25.group03.warehouseservice.dto.response.UnusedTestParameterRes;
import sum25.group03.warehouseservice.entity.enums.TestType;

import java.util.List;

public interface TestParameterService {
    List<GlobalTestParameterRes> getGlobalTestParameters(TestType testType);
    NewTestTemplate addTestTemplate(TestTemplateReq testTemplate);
    TestParameterRes addTestParameter(TestParameterReq req);
    List<TestParameterRes> getAllTestParameter();

    UnusedTestParameterRes getUnusedTestParameter(UnusedTestParameterReq unusedTestParameterReq);

}
