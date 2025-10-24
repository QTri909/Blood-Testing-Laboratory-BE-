package sum25.group03.testorderservice.component;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.parser.PipeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sum25.group03.testorderservice.entity.Parameter;
import sum25.group03.testorderservice.entity.TestOrder;
import sum25.group03.testorderservice.entity.TestResult;
import sum25.group03.testorderservice.enums.FlagStatus;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.enums.TestType;
import sum25.group03.testorderservice.repositories.ParameterRepository;
import sum25.group03.testorderservice.repositories.TestOrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class Hl7Parser {

    private final PipeParser parser = new PipeParser();

    @Autowired
    private TestOrderRepository testOrderRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    public TestResult parseHL7(String hl7Message) throws HL7Exception {
        ORU_R01 oru = (ORU_R01) parser.parse(hl7Message);

        // 🔹 Extract fields
        var obx = oru.getPATIENT_RESULT()
                .getORDER_OBSERVATION()
                .getOBSERVATION(0)
                .getOBX();

        var obr = oru.getPATIENT_RESULT()
                .getORDER_OBSERVATION()
                .getOBR();

        String testCode = obx.getObservationIdentifier().getIdentifier().getValue();
        System.out.println("testCode: " + testCode);
        String testName = obx.getObservationIdentifier().getText().getValue();
        String resultStr = obx.getObservationValue(0).getData().toString();
        String observationDate = obr.getObservationDateTime().getTime().getValue();

        Double value = null;
        if (resultStr != null && !resultStr.isEmpty()) {
            try {
                value = Double.parseDouble(resultStr);
            } catch (NumberFormatException ignored) {}
        }

        LocalDateTime createdAt = LocalDateTime.now();
        if (observationDate != null && observationDate.length() >= 12) {
            try {
                createdAt = LocalDateTime.parse(observationDate, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            } catch (Exception ignored) {}
        }

        Long instrumentId = 1L;
        Long parameterSnapshotId = 1L;
        TestType testType = TestType.valueOf(testCode.toUpperCase());
        TestResultStatus status = TestResultStatus.COMPLETED;

        String orderCode = obr.getPlacerOrderNumber().getEntityIdentifier().getValue();
        TestOrder testOrder = testOrderRepository.findById(Long.valueOf(orderCode))
                .orElseThrow(() -> new HL7Exception("Test Order Not Found"));
        Parameter parameter = parameterRepository.findByAbbreviation(testCode);

        FlagStatus flagStatus = null;
         double critialLow = parameter.getMin() * 0.8;
         double critialHigh = parameter.getMax() * 1.2;
         if(value < critialLow || value > critialHigh){
             flagStatus = FlagStatus.C;
         } else
         if(value >= parameter.getMin() && value <= parameter.getMax() ){
             flagStatus = FlagStatus.N;
         } else if(value < parameter.getMin()){
             flagStatus = FlagStatus.L;
         } else if(value > parameter.getMax()) {
             flagStatus = FlagStatus.H;
         }

        return TestResult.builder()
                .testOrder(testOrder)
                .instrumentId(instrumentId)
                .parameterSnapshotId(parameterSnapshotId)
                .flagStatus(flagStatus)
                .status(status)
                .value(value)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .testType(testType)
                .parameter(parameter)
                .build();
    }
}

