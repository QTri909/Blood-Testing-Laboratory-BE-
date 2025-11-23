package sum25.group03.testorderservice.component;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.OBR;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.entities.TestOrder;
import sum25.group03.testorderservice.entities.TestResult;
import sum25.group03.testorderservice.enums.FlagStatus;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.repositories.ParameterRepository;
import sum25.group03.testorderservice.repositories.TestOrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class Hl7Parser {

    private final PipeParser parser = new PipeParser();

    @Autowired
    private TestOrderRepository testOrderRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    private static final DateTimeFormatter HL7_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private FlagStatus calculateFlag(Parameter parameter, Double value) {
        if (parameter == null || value == null) return null;

        double criticalLow = parameter.getMin() * 0.8;
        double criticalHigh = parameter.getMax() * 1.2;

        if (value < criticalLow || value > criticalHigh) return FlagStatus.C;
        if (value >= parameter.getMin() && value <= parameter.getMax()) return FlagStatus.N;
        if (value < parameter.getMin()) return FlagStatus.L;
        return FlagStatus.H;
    }

    public List<TestResult> parseHL7(String hl7Message) {
        List<TestResult> results = new ArrayList<>();
        String[] lines = hl7Message.split("\\r?\\n");

        TestOrder testOrder = null;

        for (String line : lines) {
            String[] fields = line.split("\\|");
            if (fields.length == 0) continue;

            switch (fields[0]) {
                case "PID":
                    String barcode = fields[3];
                    testOrder = testOrderRepository.findByBarcode(barcode)
                            .orElseThrow(() -> new RuntimeException("TestOrder not found for barcode: " + barcode));
                    break;

                case "OBX":
                    if (testOrder == null) {
                        throw new RuntimeException("TestOrder must be parsed before OBX");
                    }

                    String testCode = fields[3]; // OBX-3
                    System.out.println(testCode);
                    Parameter parameter = parameterRepository.findByAbbreviation(testCode);


                    Double value = null;
                    try {
                        value = Double.parseDouble(fields[5]); // OBX-5
                    } catch (NumberFormatException ignored) {}

                    FlagStatus flag = calculateFlag(parameter, value);

                    TestResult tr = TestResult.builder()
                            .testOrder(testOrder)
                            .parameter(parameter)
                            .value(value)
                            .flagStatus(flag)
                            .status(TestResultStatus.COMPLETED)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    results.add(tr);
                    break;

                default:
                    break;
            }
        }

        return results;
    }
}

